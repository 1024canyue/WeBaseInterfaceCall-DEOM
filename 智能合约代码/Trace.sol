pragma solidity ^0.4.25;

import "./Producer.sol";
import "./Distributor.sol";
import "./Retailer.sol";
import "./FoodInfoItem.sol";

contract Trace is Producer, Distributor, Retailer {
    // 食品溯源ID到具体食品溯源合约地址映射表
    mapping(uint256 => address) foods;
    // 食品溯源ID集合
    uint256[] foodList;
    // 合约所有者
    address _owner;

    /*
    构造函数
    初始化各角色地址：生产商、分销商、零售商
    */
    constructor(
        address producer,
        address distributor,
        address retailer
    ) public Producer(producer) Distributor(distributor) Retailer(retailer) {
        _owner = msg.sender;
    }

    /*
    生产食物
    name：食品名称
    traceNumber：溯源码
    traceName：生产商名称
    quality：质量
    producer：生产商地址
    */
    function newFood(
        string name,
        uint256 traceNumber,
        string traceName,
        uint8 quality
    ) public onlyProducer returns (address) {
        require(foods[traceNumber] == address(0), "traceNumber already exists");
        FoodInfoItem food = new FoodInfoItem(
            name,
            traceName,
            quality,
            msg.sender
        );
        foods[traceNumber] = food;
        foodList.push(traceNumber);
        return food;
    }

    /*
   分销
   traceNumber：溯源码
   traceName：分销商名称
   quality：质量
   distributor：分销商地址
   */
    function distributeFood(
        uint256 traceNumber,
        string traceName,
        uint8 quality
    ) public onlyDistributor returns (address) {
        require(foods[traceNumber] != address(0), "traceNumber not exists");
        FoodInfoItem food = FoodInfoItem(foods[traceNumber]);
        food.distribute(traceName, quality, msg.sender);
        return food;
    }

    /*
    零售
    traceNumber：溯源码
    traceName：零售商名称
    quality：质量
    retailer：零售商地址
    */
    function retailFood(
        uint256 traceNumber,
        string traceName,
        uint8 quality
    ) public onlyRetailer returns (address) {
        require(foods[traceNumber] != address(0), "traceNumber not exists");
        FoodInfoItem food = FoodInfoItem(foods[traceNumber]);
        food.retail(traceName, quality, msg.sender);
        return food;
    }

    /*
    查询当前信息
    返回值：食品名称、当前阶段的用户名、当前阶段的质量、当前状态
    */
    function queryCurrentInfo(uint256 traceNumber)
    view
    returns (
        string,
        string,
        uint8,
        uint8
    )
    {
        require(foods[traceNumber] != address(0), "traceNumber not exists");
        FoodInfoItem food = FoodInfoItem(foods[traceNumber]);
        return food.queryCurrentInfo();
    }

    /*
    查询生产信息
    返回值：生产时间、生产商名称、生产商地址、质量
    */
    function queryProduceInfo(uint256 traceNumber)
    view
    returns (
        uint256,
        string,
        address,
        uint8
    )
    {
        require(foods[traceNumber] != address(0), "traceNumber not exists");
        FoodInfoItem food = FoodInfoItem(foods[traceNumber]);
        return food.queryProduceInfo();
    }
    /*
   查询分销信息
   返回值：分销时间、分销商名称、分销商地址、质量
   */
    function queryDistributeInfo(uint256 traceNumber)
    view
    returns (
        uint256,
        string,
        address,
        uint8
    )
    {
        require(foods[traceNumber] != address(0), "traceNumber not exists");
        FoodInfoItem food = FoodInfoItem(foods[traceNumber]);
        return food.queryDistributeInfo();
    }
    /*
   查询零售信息
   返回值：零售时间、零售商名称、零售商地址、质量
   */
    function queryRetailInfo(uint256 traceNumber)
    view
    returns (
        uint256,
        string,
        address,
        uint8
    )
    {
        require(foods[traceNumber] != address(0), "traceNumber not exists");
        FoodInfoItem food = FoodInfoItem(foods[traceNumber]);
        return food.queryRetailInfo();
    }
    /*
   查询所有溯源码
   */
    function queryAllTraceNumbers() view returns (uint256[]){
        return foodList;
    }
    /*
   查询指定溯源码是否存在
   */
    function queryTraceNumberExists(uint256 traceNumber) view returns(bool){
        return foods[traceNumber] != address(0);
    }
}
