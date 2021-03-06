#include <limits>

#include "bvs/info.h"


std::string BVS::Info::getFPS() const
{
	static double avgFPS = 15;
	if (avgFPS==std::numeric_limits<double>::infinity()) avgFPS = 15;

	// apply exponential smoothing with alpha = 0.2
	double duration = lastRoundDuration.count();
	duration = duration > 1000 ? 1000 : duration;
	avgFPS = (1000/duration + 4 * avgFPS)/5;

	std::string fps = std::to_string(avgFPS);
	if (fps.length()>6) fps.resize(fps.length()-5);

	return fps;
}

