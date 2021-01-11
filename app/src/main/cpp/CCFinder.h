#ifndef CCFINDER_H
#define CCFINDER_H

#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/imgproc/types_c.h>
#include <opencv2/imgcodecs.hpp>
#include "URSDefine.h"

using namespace cv;

typedef struct ConnectedComponentData
{
	cv::Point left;
	cv::Point right;
	cv::Point top;
	cv::Point bottom;
	cv::Rect boundingRect;
	int area;
} ConnComp;

class CCFinder
{
	public:
		CCFinder(cv::Mat bwImg);
		~CCFinder();
		std::vector<ConnComp> connComps;
};

#endif // CCFINDER_H