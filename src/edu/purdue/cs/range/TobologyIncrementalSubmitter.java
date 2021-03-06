package edu.purdue.cs.range;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import edu.purdue.cs.range.bolt.RangeFilterIncremental;
import edu.purdue.cs.range.grouping.DataStaticGridCustomGrouping;
import edu.purdue.cs.range.grouping.QueryStaticGridCustomGrouping;
import edu.purdue.cs.range.spout.DataGenerator;
import edu.purdue.cs.range.spout.QueryGenerator;

public class TobologyIncrementalSubmitter {





	public static void main(String[] args) throws Exception {
         
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout(Constants.objectLocationGenerator, new DataGenerator());
		builder.setSpout(Constants.queryGenerator, new QueryGenerator());
		builder.setBolt(Constants.rangeFilterBolt, new RangeFilterIncremental(),Constants.numberOfBolts)
				.customGrouping(
						Constants.queryGenerator,
						 new QueryStaticGridCustomGrouping(
								 Constants.numberOfBolts, Constants.xMaxRange,
								 Constants.yMaxRange, Constants.xCellsNum,
								 Constants.yCellsNum))
				.customGrouping(Constants.objectLocationGenerator,new DataStaticGridCustomGrouping(
						Constants.numberOfBolts, Constants.xMaxRange,
						Constants.yMaxRange, Constants.xCellsNum,
						Constants.yCellsNum));

		
        //Configuration
		Config conf = new Config();
	
		conf.setDebug(false);
        //Topology run
		conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);
		conf.put(Config.NIMBUS_HOST, "127.0.0.1");
		System.setProperty("storm.jar", "target/TrajStorm-0.0.1-SNAPSHOT.jar");
		//LocalCluster cluster = new LocalCluster();
		StormSubmitter.submitTopology("Range-Queries_toplogy", conf, builder.createTopology());
		Thread.sleep(1000);
	//	cluster.shutdown();
	}
}
