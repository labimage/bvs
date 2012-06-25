#ifndef TEST_H
#define TEST_H

#include "BVSModule.h"

// TODO comments, usage examples...
class test : public BVSModule
{
	public:
		test(const std::string identifier, const BVSConfig& config);
		~test();
		BVSStatus onLoad();
		BVSStatus preExecute();
		BVSStatus execute();
		BVSStatus postExecute();
		BVSStatus debugDisplay();
		BVSStatus onClose();

	private:
		const std::string identifier;
		BVSLogger logger;
		const BVSConfig& config;

		BVSConnector input;
		BVSConnector output;

		test(const test&) = delete; /**< -Weffc++ */
		test& operator=(const test&) = delete; /**< -Weffc++ */
};

#endif //TEST_H

