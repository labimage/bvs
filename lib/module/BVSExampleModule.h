#ifndef BVSEXAMPLEMODULE_H
#define BVSEXAMPLEMODULE_H

#include "bvs/module.h"



/** This is the BVSExampleModule class.
 * Please add sufficient documentation to enable others to use it.
 * Include information about:
 * - Dependencies
 * - Inputs
 * - Outputs
 * - Configuration Options
 */
class BVSExampleModule : public BVSModule
{
	public:
		/** Your module constructor.
		 * Please do not change the signature, as it will be called by the
		 * framework.
		 * You can use the constructor/destructor pair to create/destroy your data.
		 * @param[in] id Your modules unique identifier, will be set by framework.
		 * @param[in] config Reference to config to enable option retrieval.
		 */
		BVSExampleModule(const std::string id, const BVSConfig& config);

		/** Your module destructor. */
		~BVSExampleModule();

		/** Execute function doing all the work.
		 * This function is executed exactly once and only once upon each started
		 * round/step of the framework. It is supposed to contain the actual work
		 * of your module.
		 */
		BVS::Status execute();

		/** UNUSED
		 * @return Module's status.
		 */
		BVS::Status debugDisplay();

	private:
		const std::string id; /**< Your unique module id, set by framework. */

		/** Your logger instance.
		 * @see Logger
		 */
		BVS::Logger logger;

		/** Your config instance.
		 * @see Config
		 */
		const BVS::Config& config;

		/** Example connector used to retrieve/send data from/to other modules.
		 * @see Connector
		 */
		BVS::Connector<int> input;
		BVS::Connector<std::string> output;

		BVSExampleModule(const BVSExampleModule&) = delete; /**< -Weffc++ */
		BVSExampleModule& operator=(const BVSExampleModule&) = delete; /**< -Weffc++ */
};

#endif //BVSEXAMPLEMODULE_H

