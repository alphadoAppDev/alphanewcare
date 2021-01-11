#include "CCFinder.h"

CCFinder::CCFinder(cv::Mat bwImg)
{
	cv::Mat labelImg, stats, centroids;
	int nLabels = connectedComponentsWithStats(bwImg, labelImg, stats, centroids, 8);

	// 1st label from connectedComponentsWithStats is background.
	if (nLabels <= 1)
	{
		return;
	}

	for (int i = 1; i < nLabels; ++i)
	{
		ConnComp connComp;
		int left = stats.at<int>(i, cv::CC_STAT_LEFT);
		int top = stats.at<int>(i, cv::CC_STAT_TOP);
		int width = stats.at<int>(i, cv::CC_STAT_WIDTH);
		int height = stats.at<int>(i, cv::CC_STAT_HEIGHT);
		connComp.boundingRect = cv::Rect(left, top, width, height);
		connComp.area = stats.at<int>(i, cv::CC_STAT_AREA);
		int bottom = top + height - 1;
		int right = left + width - 1;

		for (int col = left; col < left + width; ++col)
		{
			if (labelImg.at<int>(top, col) == i)
			{
				connComp.top = cv::Point(col, top);
				break;
			}
		}

		for (int col = left; col < left + width; ++col)
		{
			if (labelImg.at<int>(bottom, col) == i)
			{
				connComp.bottom = cv::Point(col, bottom);
				break;
			}
		}

		for (int row = top; row < top + height; ++row)
		{
			if (labelImg.at<int>(row, left) == i)
			{
				connComp.left = cv::Point(left, row);
				break;
			}
		}

		for (int row = top; row < top + height; ++row)
		{
			if (labelImg.at<int>(row, right) == i)
			{
				connComp.right = cv::Point(right, row);
				break;
			}
		}

		connComps.push_back(connComp);
	}
}

CCFinder::~CCFinder()
{
	connComps.clear();
}
