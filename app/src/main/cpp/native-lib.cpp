#include <jni.h>
#include <string>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/imgproc/types_c.h>
#include <opencv2/imgcodecs.hpp>
#include "CCFinder.h"
#include "URSDefine.h"

#define angleLimit 15.0f

using namespace cv;

extern "C"
{
    typedef struct Lab2kStruct
    {
        float L;
        float a;
        float b;
    } Lab2k;

    struct DistanceRankData
    {
        int index;
        int rank;
        float distance;
    };

    struct ResultData
    {
        int type;
        std::vector<Rect> litmusRects;
        std::vector<Rect> comparisionRects;
        std::vector<std::vector<DistanceRankData>> distanceList;
    };

    float distance_rgb(Mat &img1, Mat &img2)
    {
        // https://en.wikipedia.org/wiki/Color_difference
        Scalar a = mean(img1);
        Scalar b = mean(img2);
        float distance = 0;
        float dR = a.val[2] - b.val[2];
        float dG = a.val[1] - b.val[1];
        float dB = a.val[0] - b.val[0];
        distance = powf(dR, 2) + pow(dG, 2) + pow(dB, 2);
        distance = sqrtf(distance);
        return distance;
    }

    float distance_weighted_rgb(Mat &img1, Mat &img2)
    {
        // https://en.wikipedia.org/wiki/Color_difference
        Scalar a = mean(img1);
        Scalar b = mean(img2);
        float distance = 0;
        float dR = a.val[2] - b.val[2];
        float dG = a.val[1] - b.val[1];
        float dB = a.val[0] - b.val[0];
        distance = 2 * powf(dR, 2) + 4 * pow(dG, 2) + 3 * pow(dB, 2);
        distance = sqrtf(distance);
        return distance;
    }

    float distance_weighted_rgb_ex(Mat &img1, Mat &img2)
    {
        // https://en.wikipedia.org/wiki/Color_difference
        Scalar a = mean(img1);
        Scalar b = mean(img2);
        float distance = 0;
        float meanR = (a.val[2] + b.val[2]) * 0.5f;
        float dR = a.val[2] - b.val[2];
        float dG = a.val[1] - b.val[1];
        float dB = a.val[0] - b.val[0];
        distance = (2 + meanR / 256.0f) * powf(dR, 2) + 4 * pow(dG, 2) + (2 + (255 - meanR) / 256.0f) * pow(dB, 2);
        distance = sqrtf(distance);
        return distance;
    }

    float distance_CIE1976(Mat &img1, Mat &img2)
    {
        // https://en.wikipedia.org/wiki/Color_difference
        Mat lab1, lab2;
        cvtColor(img1, lab1, COLOR_BGR2Lab);
        cvtColor(img2, lab2, COLOR_BGR2Lab);
        Scalar a = mean(lab1);
        Scalar b = mean(lab2);
        float distance = 0;
        float dL = a.val[0] - b.val[0];
        float dA = a.val[1] - b.val[1];
        float dB = a.val[2] - b.val[2];
        distance = powf(dL, 2) + pow(dA, 2) + pow(dB, 2);
        distance = sqrtf(distance);
        return distance;
    }

    float distance_CIE1994(Mat &img1, Mat &img2)
    {
        // https://en.wikipedia.org/wiki/Color_difference
        Mat labImg1, labImg2;
        cvtColor(img1, labImg1, COLOR_BGR2Lab);
        cvtColor(img2, labImg2, COLOR_BGR2Lab);
        Scalar a = mean(labImg1);
        Scalar b = mean(labImg2);
        Lab2k Lab1;
        Lab1.L = a[0];
        Lab1.a = a[1];
        Lab1.b = a[2];
        Lab2k Lab2;
        Lab2.L = b[0];
        Lab2.a = b[1];
        Lab2.b = b[2];

        // for textiles
        float KL = 2;
        float K1 = 0.048;
        float K2 = 0.014;

        float KC = 1;
        float KH = 1;

        float dL = Lab1.L - Lab2.L;
        float C1 = sqrtf(powf(Lab1.a, 2) + powf(Lab1.b, 2));
        float C2 = sqrtf(powf(Lab2.a, 2) + powf(Lab2.b, 2));
        float dCab = C1 - C2;
        float da = Lab1.a - Lab2.a;
        float db = Lab1.b - Lab2.b;
        float dHab = sqrtf(powf(da, 2) + powf(db, 2) - powf(dCab, 2));
        float SL = 1;
        float SC = 1 + K1 * C1;
        float SH = 1 + K2 * C1;
        float dE94 = sqrtf(powf(dL/(KL*SL), 2) + powf(dCab/(KC*SC), 2) + powf(dHab/(KH*SH), 2));

        return dE94;
    }

    float distance_CIE2000(Mat &img1, Mat &img2)
    {
        // https://en.wikipedia.org/wiki/Color_difference
        Mat labImg1, labImg2;
        cvtColor(img1, labImg1, COLOR_BGR2Lab);
        cvtColor(img2, labImg2, COLOR_BGR2Lab);
        Scalar a = mean(labImg1);
        Scalar b = mean(labImg2);
        Lab2k Lab1;
        Lab1.L = a[0];
        Lab1.a = a[1];
        Lab1.b = a[2];
        Lab2k Lab2;
        Lab2.L = b[0];
        Lab2.a = b[1];
        Lab2.b = b[2];

        float kL = 1.0;
        float kC = 1.0;
        float kH = 1.0;
        float lBarPrime = 0.5 * (Lab1.L + Lab2.L);
        float c1 = sqrtf(Lab1.a * Lab1.a + Lab1.b * Lab1.b);
        float c2 = sqrtf(Lab2.a * Lab2.a + Lab2.b * Lab2.b);
        float cBar = 0.5 * (c1 + c2);
        float cBar7 = cBar * cBar * cBar * cBar * cBar * cBar * cBar;
        float g = 0.5 * (1.0 - sqrtf(cBar7 / (cBar7 + 6103515625.0)));  /* 6103515625 = 25^7 */
        float a1Prime = Lab1.a * (1.0 + g);
        float a2Prime = Lab2.a * (1.0 + g);
        float c1Prime = sqrtf(a1Prime * a1Prime + Lab1.b * Lab1.b);
        float c2Prime = sqrtf(a2Prime * a2Prime + Lab2.b * Lab2.b);
        float cBarPrime = 0.5 * (c1Prime + c2Prime);
        float h1Prime = (atan2f(Lab1.b, a1Prime) * 180.0) / M_PI;
        float dhPrime; // not initialized on purpose

        if (h1Prime < 0.0)
            h1Prime += 360.0;
        float h2Prime = (atan2f(Lab2.b, a2Prime) * 180.0) / M_PI;
        if (h2Prime < 0.0)
            h2Prime += 360.0;
        float hBarPrime = (fabsf(h1Prime - h2Prime) > 180.0) ? (0.5 * (h1Prime + h2Prime + 360.0)) : (0.5 * (h1Prime + h2Prime));
        float t = 1.0 -
                  0.17 * cosf(M_PI * (      hBarPrime - 30.0) / 180.0) +
                  0.24 * cosf(M_PI * (2.0 * hBarPrime       ) / 180.0) +
                  0.32 * cosf(M_PI * (3.0 * hBarPrime +  6.0) / 180.0) -
                  0.20 * cosf(M_PI * (4.0 * hBarPrime - 63.0) / 180.0);
        if (fabsf(h2Prime - h1Prime) <= 180.0)
            dhPrime = h2Prime - h1Prime;
        else
            dhPrime = (h2Prime <= h1Prime) ? (h2Prime - h1Prime + 360.0) : (h2Prime - h1Prime - 360.0);
        float dLPrime = Lab2.L - Lab1.L;
        float dCPrime = c2Prime - c1Prime;
        float dHPrime = 2.0 * sqrtf(c1Prime * c2Prime) * sinf(M_PI * (0.5 * dhPrime) / 180.0);
        float sL = 1.0 + ((0.015 * (lBarPrime - 50.0) * (lBarPrime - 50.0)) / sqrtf(20.0 + (lBarPrime - 50.0) * (lBarPrime - 50.0)));
        float sC = 1.0 + 0.045 * cBarPrime;
        float sH = 1.0 + 0.015 * cBarPrime * t;
        float dTheta = 30.0 * expf(-((hBarPrime - 275.0) / 25.0) * ((hBarPrime - 275.0) / 25.0));
        float cBarPrime7 = cBarPrime * cBarPrime * cBarPrime * cBarPrime * cBarPrime * cBarPrime * cBarPrime;
        float rC = sqrtf(cBarPrime7 / (cBarPrime7 + 6103515625.0));
        float rT = -2.0 * rC * sinf(M_PI * (2.0 * dTheta) / 180.0);

        float dE2000 = sqrtf(
                (dLPrime / (kL * sL)) * (dLPrime / (kL * sL)) +
                (dCPrime / (kC * sC)) * (dCPrime / (kC * sC)) +
                (dHPrime / (kH * sH)) * (dHPrime / (kH * sH)) +
                (dCPrime / (kC * sC)) * (dHPrime / (kH * sH)) * rT);

        return dE2000;
    }

    float getAngle(Point p1, Point p2)
    {
        float dx = (p1.x - p2.x);
        float dy = (p1.y - p2.y);

        float angle = atan2(dx, dy) * 180 / CV_PI;
        return angle;
    }

    JNIEXPORT jint JNICALL
    Java_kr_co_alphacare_ImageProcessing_detectURS(
            JNIEnv *env, jclass type,
            jintArray imageData_, jint width, jint height, jint mode)
    {
        jint *imageData = env->GetIntArrayElements(imageData_, NULL);
		
        int rtnType = 0;

        // 이미지 생성
        Mat src(height, width, CV_8UC4, (unsigned char *) imageData);

        env->ReleaseIntArrayElements(imageData_, imageData, 0);

        // rgb -> gray 변환
        Mat bw;
        cvtColor(src, bw, COLOR_RGBA2GRAY);

        int windowSize = min(src.cols, src.rows) * 0.03;
        windowSize = max(windowSize, 5);
        if (windowSize % 2 == 0)
        {
            windowSize++;
        }

        // 네모 변 길이 : 추후 수정 가능
        // 이미지 짧은축 길이 기준
        // 짧은 축 길이가 868일때 사각형은 70 정도.
        int rectMinTh = min(src.cols, src.rows) * 0.03; // 244 : 12
        int rectMaxTh = 0;
		switch (mode)
		{
            case 1:

                rectMaxTh = min(src.cols, src.rows) * 0.13; // 244 : 27
                break;
            case 2:
            case 3:
            case 4:
                rectMaxTh = min(src.cols, src.rows) * 0.18; // 244 : 43
                break;
            default:
                return 0;
                break;
		}

//		LOGD("rectMinTh : %d, rectMaxTh : %d", rectMinTh, rectMaxTh);

        adaptiveThreshold(bw, bw, 255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY_INV, windowSize, 15);

        CCFinder* ccfinder = new CCFinder(bw);

        std::vector<cv::Rect> rects;

        for (int i = 0; i < ccfinder->connComps.size(); ++i)
        {
            cv::Rect rect = ccfinder->connComps.at(i).boundingRect;
            float whRatio = min(rect.width, rect.height) / (float)max(rect.width, rect.height);

            if (0.9f < whRatio && rectMinTh < rect.height && rect.height < rectMaxTh)
            {
                rects.push_back(rect);
            }
        }

        cv::Rect eraseRect = rects.size() > 0 ? rects.at(0) : cv::Rect(0, 0, 0, 0);
        eraseRect.y = eraseRect.br().y + 1;
        eraseRect.height = bw.rows - eraseRect.y;
        cv::Rect frameRectSize(0, 0, bw.cols, bw.rows);
        eraseRect = eraseRect & frameRectSize;
        rectangle(bw, eraseRect, Scalar(0), -1);

        if (ccfinder != NULL)
        {
            delete ccfinder;
            ccfinder = NULL;
        }

        ccfinder = new CCFinder(bw);

        rects.clear();
        for (int i = 0; i < ccfinder->connComps.size(); ++i)
        {
            cv::Rect rect = ccfinder->connComps.at(i).boundingRect;
            float whRatio = min(rect.width, rect.height) / (float)max(rect.width, rect.height);

            if (0.9f < whRatio && rectMinTh < rect.height && rect.height < rectMaxTh)
            {
                rects.push_back(rect);
            }
        }

        if (ccfinder != NULL)
        {
            delete ccfinder;
            ccfinder = NULL;
        }


        if ((mode == 1 && rects.size() < 30) ||
        //if ((mode == 1 && rects.size() != 52) ||
            (mode == 2 && rects.size() != 5) ||
            (mode == 3 && rects.size() != 7) ||
            (mode == 4 && rects.size() != 5))
        {
			LOGD("사각형 갯수 미달 - mode didn't be matched");
            rtnType = -1;
		}
        else
        {
            Rect rightTop = rects.at(1).x > rects.at(2).x ? rects.at(1) : rects.at(2);
            Rect rightBottom = rects.at(rects.size() - 2).x > rects.at(rects.size() - 1).x ? rects.at(rects.size() - 2) : rects.at(rects.size() - 1);

            bool intersect = false;
            for (int i = 1; i < rects.size(); ++i)
            {
                Rect firstRect = rects.at(0);
                firstRect.x = 0;
                firstRect.width = bw.cols;

                Rect rect = rects.at(i);

                if (!(rect & firstRect).empty())
                {
                    intersect = true;
                    break;
                }
            }


            if (rects.at(0).y > bw.rows * 0.25f ||
                rects.at(0).x < bw.cols * 0.3f ||
                rects.at(0).x > bw.cols * 0.7f ||
                intersect)
            {
                LOGD("용지 방향 확인 필요");
                // 첫 렉트는 상단에 존재해야 함.
                rtnType = -2;
            }
            else
            {
                float angle = rightBottom == rightTop ? 200.0f : getAngle(rightBottom.tl(), rightTop.tl());

                LOGD("angle : %.2f", fabs(angle));
                if (fabs(angle) > angleLimit)
                {
                    LOGD("각도 확인 필요");
                    return -1;
                }
                else
                {
                    Rect frame = rects.at(1);

                    for (int i = 2; i < rects.size(); ++i)
                    {
                        frame |= rects.at(i);
                    }

                    rects.erase(rects.begin());

                    Rect litmusFrameRect(bw.cols / 2 - (rects.at(0).width / 2), frame.y, rects.at(0).width, frame.height);
                    Mat cropPaperPart = src(litmusFrameRect);
                    Mat cropPaperPartBGR;
                    cvtColor(cropPaperPart, cropPaperPartBGR, COLOR_RGBA2BGR);
                    Rect litmusRect = rects.at(0);
                    litmusRect.x = cropPaperPart.cols / 2 - litmusRect.width / 4;
                    litmusRect.y = litmusRect.height / 2;
                    litmusRect.width = litmusRect.width / 2;
                    litmusRect.height = litmusRect.height / 2;

                    Mat litmusHeadBgr = cropPaperPartBGR(litmusRect);
                    Mat blackHeadBgr = Mat(10, 10, CV_8UC3, Scalar(3));
                    float dist = distance_rgb(litmusHeadBgr, blackHeadBgr);

                    LOGD("리트머스 용지가 색상차이 %.2f", dist);
                    if (dist < 100.0f)
                    {
                        LOGD("리트머스 용지가 없음 %.2f", dist);
                        rtnType = -1;
                    }
                    else
                    {
                        int maxRectCount;

                        switch (mode)
                        {
                            case 1:
                                maxRectCount = 53;
                                break;
                            case 2:
                                maxRectCount = 4;
                                break;
                            case 3:
                                maxRectCount = 6;
                                break;
                            default: // 4
                                maxRectCount = 4;
                        }

                        if (rects.size() == maxRectCount)
                        {
                            LOGD("Detect 성공");
                            rtnType = 1;
                        }
                        else
                        {
                            //LOGD("사각형 갯수 미달");
                            rtnType = -1;
                        }
                    }
                }
            }
        }

        env->ReleaseIntArrayElements(imageData_, imageData, 0);

        return rtnType;
    }

    jobject convertResultDataCpp2Java(JNIEnv *env, ResultData resultData)
    {
        // 전체 결과 데이터 객체 생성
        jclass resultDataClass = env->FindClass("kr/co/alphacare/result/model/ResultData");
        jmethodID initMethodId = env->GetMethodID(resultDataClass, "<init>", "()V");
        jobject _resultData = env->NewObject(resultDataClass, initMethodId);

        jfieldID fid = env->GetFieldID(resultDataClass, "type", "I");
        env->SetIntField(_resultData, fid, resultData.type);

        // 리트머스 용지
        fid = env->GetFieldID(resultDataClass, "litmusRects", "Ljava/util/ArrayList;");
        jobject litmusRectList = env->GetObjectField(_resultData, fid);
        jclass litmusRectListClass = env->GetObjectClass(litmusRectList);
        jmethodID litmusRectListAdd = env->GetMethodID(litmusRectListClass, "add", "(Ljava/lang/Object;)Z");

        jfieldID rectRieldId;
        for(int i = 0; i < resultData.litmusRects.size(); i++)
        {
            jclass rectClass = env->FindClass("android/graphics/Rect");
            jmethodID rectInit = env->GetMethodID(rectClass, "<init>", "()V");
            jobject rect = env->NewObject(rectClass, rectInit);

            int x = resultData.litmusRects.at(i).x;
            int y = resultData.litmusRects.at(i).y;
            int w = resultData.litmusRects.at(i).width;
            int h = resultData.litmusRects.at(i).height;

            rectRieldId = env->GetFieldID(rectClass, "left", "I");
            env->SetIntField(rect, rectRieldId, x);
            rectRieldId = env->GetFieldID(rectClass, "top", "I");
            env->SetIntField(rect, rectRieldId, y);
            rectRieldId = env->GetFieldID(rectClass, "right", "I");
            env->SetIntField(rect, rectRieldId, x + w);
            rectRieldId = env->GetFieldID(rectClass, "bottom", "I");
            env->SetIntField(rect, rectRieldId, y + h);

            env->CallBooleanMethod(litmusRectList, litmusRectListAdd, rect);

            env->DeleteLocalRef(rectClass);
            env->DeleteLocalRef(rect);
        }

        env->SetObjectField(_resultData, fid, litmusRectList);

        env->DeleteLocalRef(litmusRectListClass);
        env->DeleteLocalRef(litmusRectList);

        // 비교 용지
        fid = env->GetFieldID(resultDataClass, "comparisionRects", "Ljava/util/ArrayList;");
        jobject comparisionRectList = env->GetObjectField(_resultData, fid);
        jclass comparisionRectListClass = env->GetObjectClass(comparisionRectList);
        jmethodID comparisionRectListAdd = env->GetMethodID(comparisionRectListClass, "add", "(Ljava/lang/Object;)Z");

        for(int i = 0; i < resultData.comparisionRects.size(); i++)
        {
            jclass rectClass = env->FindClass("android/graphics/Rect");
            jmethodID rectInit = env->GetMethodID(rectClass, "<init>", "()V");
            jobject rect = env->NewObject(rectClass, rectInit);

            int x = resultData.comparisionRects.at(i).x;
            int y = resultData.comparisionRects.at(i).y;
            int w = resultData.comparisionRects.at(i).width;
            int h = resultData.comparisionRects.at(i).height;

            rectRieldId = env->GetFieldID(rectClass, "left", "I");
            env->SetIntField(rect, rectRieldId, x);
            rectRieldId = env->GetFieldID(rectClass, "top", "I");
            env->SetIntField(rect, rectRieldId, y);
            rectRieldId = env->GetFieldID(rectClass, "right", "I");
            env->SetIntField(rect, rectRieldId, x + w);
            rectRieldId = env->GetFieldID(rectClass, "bottom", "I");
            env->SetIntField(rect, rectRieldId, y + h);

            env->CallBooleanMethod(comparisionRectList, comparisionRectListAdd, rect);

            env->DeleteLocalRef(rectClass);
            env->DeleteLocalRef(rect);
        }

        env->SetObjectField(_resultData, fid, comparisionRectList);

        env->DeleteLocalRef(comparisionRectListClass);
        env->DeleteLocalRef(comparisionRectList);

        // 거리 계산 결과
        fid = env->GetFieldID(resultDataClass, "distanceList", "Ljava/util/ArrayList;");
        jobject distanceList = env->GetObjectField(_resultData, fid);
        jclass distanceListClass = env->GetObjectClass(distanceList);
        jmethodID distanceListAdd = env->GetMethodID(distanceListClass, "add", "(Ljava/lang/Object;)Z");

        for(int i = 0; i < resultData.distanceList.size(); i++)
        {
            jclass arrayListClass = env->FindClass("java/util/ArrayList");
            jmethodID arrayListInit = env->GetMethodID(arrayListClass, "<init>", "()V");
            jmethodID arrayListAdd = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");
            jobject arrayList = env->NewObject(arrayListClass, arrayListInit);

            std::vector<DistanceRankData> distanceDatas = resultData.distanceList.at(i);

            for (int j = 0; j < distanceDatas.size(); ++j)
            {
                jclass distanceRankDataClass = env->FindClass("kr/co/alphacare/result/model/DistanceRankData");

                jmethodID distanceRankDataInit = env->GetMethodID(distanceRankDataClass, "<init>", "(IF)V");
                jobject distanceRankData = env->NewObject(
                        distanceRankDataClass,
                        distanceRankDataInit,
                        distanceDatas.at(j).index,
                        distanceDatas.at(j).distance);

                env->CallBooleanMethod(arrayList, arrayListAdd, distanceRankData);

                env->DeleteLocalRef(distanceRankDataClass);
                env->DeleteLocalRef(distanceRankData);
            }

            env->CallBooleanMethod(distanceList, distanceListAdd, arrayList);

            env->DeleteLocalRef(arrayListClass);
            env->DeleteLocalRef(arrayList);
        }

        env->DeleteLocalRef(distanceListClass);
        env->DeleteLocalRef(distanceList);

        return _resultData;
    }

    JNIEXPORT jobject JNICALL
    Java_kr_co_alphacare_ImageProcessing_findURSResult(
            JNIEnv *env, jclass type,
            jstring imagePath_, jint cropX, jint cropY, jint cropW, jint cropH, jint mode)
    {
        const char *imagePath = env->GetStringUTFChars(imagePath_, 0);

        // 원본 이미지 경로로 부터 읽어오기
        Mat src = imread(imagePath);

        src = src(Rect(cropX, cropY, cropW, cropH));

        // 가이드 라인 기준으로 자른 이미지로 원본이미지 변경
        imwrite(imagePath, src);

        env->ReleaseStringUTFChars(imagePath_, imagePath);

        int rtnType = 0;

        // rgb -> gray 변환
        Mat bw;
        cvtColor(src, bw, COLOR_RGBA2GRAY);

        int windowSize = min(src.cols, src.rows) * 0.03;
        windowSize = max(windowSize, 5);
        if (windowSize % 2 == 0)
        {
            windowSize++;
        }

        // 네모 변 길이 : 추후 수정 가능
        // 이미지 짧은축 길이 기준
        // 짧은 축 길이가 868일때 사각형은 70 정도.
        int rectMinTh = min(src.cols, src.rows) * 0.03; // 244 : 12
        int rectMaxTh = 0;
		switch (mode)
		{
            case 1:

                rectMaxTh = min(src.cols, src.rows) * 0.13; // 244 : 27
                break;
            case 2:
            case 3:
            case 4:
                rectMaxTh = min(src.cols, src.rows) * 0.18;
                break;
            default:
                return 0;
                break;
		}
        LOGD("rectMinTh : %d, rectMaxTh : %d", rectMinTh, rectMaxTh);

        adaptiveThreshold(bw, bw, 255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY_INV, windowSize, 15);

        CCFinder* ccfinder = new CCFinder(bw);

        std::vector<cv::Rect> rects;

        for (int i = 0; i < ccfinder->connComps.size(); ++i)
        {
            cv::Rect rect = ccfinder->connComps.at(i).boundingRect;
            float whRatio = min(rect.width, rect.height) / (float)max(rect.width, rect.height);

            if (0.9f < whRatio && rectMinTh < rect.height && rect.height < rectMaxTh)
            {
                rects.push_back(rect);
            }
        }

        cv::Rect eraseRect = rects.size() > 0 ? rects.at(0) : cv::Rect(0, 0, 0, 0);
        eraseRect.y = eraseRect.br().y + 1;
        eraseRect.height = bw.rows - eraseRect.y;
        cv::Rect frameRectSize(0, 0, bw.cols, bw.rows);
        eraseRect = eraseRect & frameRectSize;
        rectangle(bw, eraseRect, Scalar(0), -1);

        if (ccfinder != NULL)
        {
            delete ccfinder;
            ccfinder = NULL;
        }

        ccfinder = new CCFinder(bw);

        rects.clear();
        for (int i = 0; i < ccfinder->connComps.size(); ++i)
        {
            cv::Rect rect = ccfinder->connComps.at(i).boundingRect;
            float whRatio = min(rect.width, rect.height) / (float)max(rect.width, rect.height);

            if (0.9f < whRatio && rectMinTh < rect.height && rect.height < rectMaxTh)
            {
                rects.push_back(rect);
            }
        }

        std::vector<Rect> testRects;

        if (ccfinder != NULL)
        {
            delete ccfinder;
            ccfinder = NULL;
        }

        if ((mode == 1 && rects.size() < 30) ||
        //if ((mode == 1 && rects.size() != 52) ||
            (mode == 2 && rects.size() != 5) ||
            (mode == 3 && rects.size() != 7) ||
            (mode == 4 && rects.size() != 5))
        {
            //LOGD("사각형 갯수 미달");
            rtnType = -1;
		}
        else
        {
            Rect rightTop = rects.at(1).x > rects.at(2).x ? rects.at(1) : rects.at(2);
            Rect rightBottom = rects.at(rects.size() - 2).x > rects.at(rects.size() - 1).x ? rects.at(rects.size() - 2) : rects.at(rects.size() - 1);

            bool intersect = false;
            for (int i = 1; i < rects.size(); ++i)
            {
                Rect firstRect = rects.at(0);
                firstRect.x = 0;
                firstRect.width = bw.cols;

                Rect rect = rects.at(i);

                if (!(rect & firstRect).empty())
                {
                    intersect = true;
                    break;
                }
            }

            if (rects.at(0).y > bw.rows * 0.25f ||
                rects.at(0).x < bw.cols * 0.3f ||
                rects.at(0).x > bw.cols * 0.7f ||
                intersect == true)
            {
                LOGD("용지 방향 확인 필요");
                // 마지막 렉트는 상단에 존재해야 함.
                rtnType = -2;
            }
            else
            {
                float angle = rightBottom == rightTop ? 200.0f : getAngle(rightBottom.tl(), rightTop.tl());

                LOGD("angle : %.2f", fabs(angle));
                if (fabs(angle) > angleLimit)
                {
                    rtnType = -1;
                }
                else
                {
                    Rect frame = rects.at(1);

                    for (int i = 2; i < rects.size(); ++i)
                    {
                        frame |= rects.at(i);
                    }

                    rects.erase(rects.begin());

                    Rect litmusFrameRect(bw.cols / 2 - (rects.at(0).width / 2), frame.y, rects.at(0).width, frame.height);
                    Mat cropPaperPart = src(litmusFrameRect);
                    Mat cropPaperPartBGR;
                    cvtColor(cropPaperPart, cropPaperPartBGR, COLOR_RGBA2BGR);
                    Rect litmusRect = rects.at(0);
                    litmusRect.x = cropPaperPart.cols / 2 - litmusRect.width / 4;
                    litmusRect.y = litmusRect.height / 2;
                    litmusRect.width = litmusRect.width / 2;
                    litmusRect.height = litmusRect.height / 2;
                    Mat litmusHeadBgr = cropPaperPartBGR(litmusRect);
                    Mat blackHeadBgr = Mat(10, 10, CV_8UC3, Scalar(3));
                    float dist = distance_rgb(litmusHeadBgr, blackHeadBgr);

                    LOGD("리트머스 용지가 색상차이 %.2f", dist);
                    if (dist < 100.0f)
                    {
                        LOGD("리트머스 용지가 없음 %.2f", dist);
                        rtnType = -1;
                    }
                    else
                    {
                        int maxRectCount;

                        switch (mode)
                        {
                            case 1:
                                maxRectCount = 53;
                                break;
                            case 2:
                                maxRectCount = 4;
                                break;
                            case 3:
                                maxRectCount = 6;
                                break;
                            default: // 4
                                maxRectCount = 4;
                        }

                        if (rects.size() == maxRectCount)
                        {
                            LOGD("Detect 성공");
                            rtnType = 1;
                        }
                        else
                        {
                            //LOGD("사각형 갯수 미달");
                            rtnType = -1;
                        }

                        // sort rects . . .
                        for (int i = 0; i < rects.size(); ++i)
                        {
                            Rect rect1 = rects.at(i);

                            Rect rect1h = rect1;
                            rect1h.x = 0;
                            rect1h.width = bw.cols;

                            Rect rect1v = rect1;
                            rect1v.y = 0;
                            rect1v.height = bw.rows;

                            for (int j = 0; j < rects.size(); ++j)
                            {
                                Rect rect2 = rects.at(j);

                                // horizontal intersection
                                if (!(rect1v & rect2).empty())
                                {
                                    Rect interRect = (rect1v & rect2);
                                    rect1.x = interRect.x;
                                    rect1.width = interRect.br().x - interRect.x + 1;
                                    rect1v.x = rect1.x;
                                    rect1v.width = rect1.width;
                                }

                                // vertical intersection
                                if (!(rect1h & rect2).empty())
                                {
                                    Rect interRect = (rect1h & rect2);
                                    rect1.y = interRect.y;
                                    rect1.height = interRect.br().y - interRect.y + 1;
                                    rect1h.y = rect1.y;
                                    rect1h.height = rect1.height;
                                }
                            }

                            rects.at(i) = rect1;
                        }

                        bw.setTo(Scalar(0));

                        for (int i = 0; i < rects.size(); ++i)
                        {
                            rectangle(bw, rects.at(i), Scalar(255), -1);
                        }

                        ccfinder = new CCFinder(bw);
                        rects.clear();

                        for (int i = 0; i < ccfinder->connComps.size(); ++i)
                        {
                            rects.push_back(ccfinder->connComps.at(i).boundingRect);
                        }

                        if (ccfinder != NULL)
                        {
                            delete ccfinder;
                            ccfinder = NULL;
                        }

						if (mode == 1)
						{
							Rect innerFrame = frame;
							innerFrame.y += rects.at(0).height / 2;
							innerFrame.height -= rects.at(0).height;
							int rectH = rects.at(0).height;// *0.5f;
							float gapH = (innerFrame.height - 10 * rectH) / (float)9;
							Rect rect = innerFrame;
							rect.x = frame.x + frame.width / 2 - rectH / 2;
							rect.height = rectH;
							rect.width = rectH;
							//rectangle(src, rect, Scalar(33, 33, 33), 2);
							testRects.push_back(rect);

							for (int k = 0; k < 9; ++k)
							{
								rect.y = rect.y + rect.height + gapH;
								rect.height = rectH;
								//rectangle(src, rect, Scalar(33, 33, 33), 2);
								testRects.push_back(rect);
							}
						}
                        // mode 2, 3, 4 (diet test)
						else if (mode == 2 || mode == 4)
						{
							Rect rect = frame;
							Point center(rect.x + rect.width / 2, rect.y + rect.height / 2);
							rect.x = center.x;
							rect.y = center.y;
							rect.width = rects.at(0).width;
							rect.height = rects.at(0).height;
							rect.x -= rect.width / 2;
							rect.y -= rect.height / 2;
							rect.y += (int)(rect.height * 0.1f);

							testRects.push_back(rect);
						} else if (mode == 3) {
                            Rect rect = frame;
                            rect.height *= (158/(float)253);
                            Point center(rect.x + rect.width / 2, rect.y + rect.height / 2);
                            rect.x = center.x;
                            rect.y = center.y;
                            rect.width = rects.at(0).width;
                            rect.height = rects.at(0).height;
                            rect.x -= rect.width / 2;
                            rect.y -= rect.height / 2;
                            rect.y += (int)(rect.height * 0.1f);

                            testRects.push_back(rect);
						}
                    }
                }
            }
        }

        ResultData resultData;

        if (rtnType >= 1)
        {
            for (int i = 0; i < testRects.size(); ++i)
            {
                // 리트머스 rect
                Rect resultRect = testRects.at(i);

                resultRect.x += resultRect.width / 4;
                resultRect.y += resultRect.height / 4;
                resultRect.width /= 2;
                resultRect.height /= 2;

                Mat bgr;
                cvtColor(src, bgr, COLOR_RGBA2BGR);
                Mat bgrCrop1 = bgr(resultRect);
    //                LOGD("bgrCrop1 : %d, bgrCrop1 : %d", bgrCrop1.cols, bgrCrop1.rows);
                resultData.litmusRects.push_back(resultRect);

                // 거리값
                std::vector<DistanceRankData> distanceResults;

                for (int j = 0; j < rects.size(); ++j)
                {
                    Rect rect2 = rects.at(j);
                    Point p2(rect2.x + rect2.width / 2, rect2.y + rect2.height / 2);
                    rect2.x = p2.x - 1;
                    rect2.width = 3;
                    rect2.y = p2.y - 1;
                    rect2.height = 3;

                    Mat bgrCrop2 = bgr(rect2);

                    DistanceRankData distanceRankData;
                    distanceRankData.index = j;
    //                    distanceRankData.distance = distance_rgb(bgrCrop1, bgrCrop2); // here
    //                    distanceRankData.distance = distance_rgb(bgrCrop1, bgrCrop2); // here
    //                    distanceRankData.distance = distance_weighted_rgb_ex(bgrCrop1, bgrCrop2); // here
                    distanceRankData.distance = distance_CIE1976(bgrCrop1, bgrCrop2); // here
    //                    distanceRankData.distance = distance_CIE1994(bgrCrop1, bgrCrop2); // here
    //                    distanceRankData.distance = distance_CIE2000(bgrCrop1, bgrCrop2); // here

                    distanceResults.push_back(distanceRankData);
                }

                resultData.distanceList.push_back(distanceResults);
            }

            // 비교 용지 rect
            for (int j = 0; j < rects.size(); ++j)
            {
                Rect comparisionRect = rects.at(j);
                resultData.comparisionRects.push_back(comparisionRect);
            }
        }

        resultData.type = rtnType;

        return convertResultDataCpp2Java(env, resultData);
    }

    JNIEXPORT jstring
    JNICALL
    Java_kr_co_alphacare_ImageProcessing_getVersion(JNIEnv *env, jclass type)
    {
        std::string version = "ver 1.0.190811";
        return env->NewStringUTF(version.c_str());
    }
}