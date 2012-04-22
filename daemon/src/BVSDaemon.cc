#include "stdio.h"
#include "BVS.h"

int main(int argc, char** argv)
{
    // start BVS
    BVS bvs(argc, argv);
    bvs.enableLogFile("BVSLog.txt");

    BVSLogger logger("BVSDaemon");
    LOG(2, "starting!");

    //LOG(2, "dump all config options!");
    //bvs.config.showOptionStore();

    LOG(2, "loading Modules!");
    bvs.loadModules();

    LOG(2, "run!");
    //bvs.run();

    //std::string s = "core.list";
    //std::vector<int> foo;
    //bvs.config.getValue(s, foo);
    //for (auto it : foo)
    //{
        //std::cout << foo[it] << std::endl;
    //}

    return 0;
}
