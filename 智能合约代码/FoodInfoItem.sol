pragma solidity ^0.4.25;

contract FoodInfoItem {
    // 食品流转过程中各个阶段的时间戳
    uint256[] _timestamp;
    // 食品流转过程中各个阶段的用户名
    string[] _traceName;
    // 食品流转过程中各个阶段的质量
    uint8[] _traceQuality;
    // 食品流转过程中各个阶段的用户地址
    address[] _traceAddress;
    // 食品名称
    string _name;
    // 当前阶段的用户名
    string _currentTraceName;
    // 当前质量（0：优质，1：良好，2：合格，3：不合格）
    uint8 _quality;
    // 当前状态（0：生产，1：分销，2：出售）
    uint8 _status;
    // 合约所有者
    address _owner;
    
    /*
    构造函数
    name：食品名称
    traceName：生产商名称
    quality：质量
    producer：生产商
    */
    constructor(
        string name,
        string traceName,
        uint8 quality,
        address producer
    ) public {
        _timestamp.push(now);
        _traceName.push(traceName);
        _traceQuality.push(quality);
        _traceAddress.push(producer);
        _name = name;
        _currentTraceName = traceName;
        _quality = quality;
        _status = 0;
        _owner = msg.sender;
    }

    /*
    分销
    traceName：分销商名称
    quality：质量
    distributor：分销商
    */
    function distribute(string traceName, uint8 quality, address distributor){
        require(_status == 0, "Status Error");
        _timestamp.push(now);
        _traceName.push(traceName);
        _traceQuality.push(quality);
        _traceAddress.push(distributor);
        _currentTraceName = traceName;
        _quality = quality;
        _status = 1;
    }
    
    /*
    零售
    traceName：零售商名称
    quality：质量
    retailer：零售商
    */
    function retail(string traceName, uint8 quality, address retailer){
        require(_status == 1, "Status Error");
        _timestamp.push(now);
        _traceName.push(traceName);
        _traceQuality.push(quality);
        _traceAddress.push(retailer);
        _currentTraceName = traceName;
        _quality = quality;
        _status = 2;
    }
    
    /*
    查询当前信息
    返回值：食品名称、当前阶段的用户名、当前阶段的质量、当前状态
    */
    function queryCurrentInfo() view returns (string, string, uint8, uint8){
        return (_name, _currentTraceName, _quality, _status);
    }
    
    /*
   查询生产信息
   返回值：生产时间、生产商名称、生产商地址、质量
   */
    function queryProduceInfo() view returns (uint256, string, address, uint8){
        require(_status >= 0, "Status Error");
        return (_timestamp[0], _traceName[0], _traceAddress[0], _traceQuality[0]);
    }
    
    /*
    查询分销信息
    返回值：分销时间、分销商名称、分销商地址、质量
    */
    function queryDistributeInfo() view returns (uint256, string, address, uint8){
        require(_status >= 1, "Status Error");
        return (_timestamp[1], _traceName[1], _traceAddress[1], _traceQuality[1]);
    }
    
    /*
   查询零售信息
   返回值：零售时间、零售商名称、零售商地址、质量
   */
    function queryRetailInfo() view returns (uint256, string, address, uint8){
        require(_status >= 2, "Status Error");
        return (_timestamp[2], _traceName[2], _traceAddress[2], _traceQuality[2]);
    }
    
}
