var sql = require('mysql');
var SQLinfo = require('../config/db/db_info');
var pool = sql.createPool(SQLinfo);
var format = require('date-format');
var logger = require('./logModules.js');

function checkUserGotUL(userId,callback) //userlocation 에 해당 user row가 있는지 검사
{
    var queryString = 'select ul_id from userlocation where ul_id = ?;';
    
    pool.getConnection((err,connection)=>
    {
        if(!err){
            connection.query(queryString,[userId],(err2,rows)=>
            { //somehow always err2 return null, even its failed.
                connection.release();
                if(!err2 && rows.length===1){
                    callback(null); //success
                }else{
                    callback('mysql err :empty userId in userlocation, userId='+userId);//failed
                }
            });
        }else{
            callback(err);          //failed
        }
    });
}
function createUserULInfo(userId,callback) //userlocation에 해당 유저의 row를 추가하는 함수
{
    var queryString = 'insert into userlocation (ul_id) values(?);';
    pool.getConnection((err,connection)=>
    {
        if(!err){
            connection.query(queryString,[userId],(err,rows)=>
            {
                connection.release();
                if(!err){
                    callback(null); //success
                }else{
                    callback(err);  //failed
                }
            });
        }else{
            callback(err);          //failed
        }
    });
}
function addUserULInfo(userId,location,callback) //userlocation 에 해당유저 정보를 업데이트하는 함수
{
    var queryString = 'update userlocation set ul_gps_lat=?, ul_gps_lon=? where ul_id=?;';
    pool.getConnection((err,connection)=>
    {
        if(!err){
            connection.query(queryString,[location.lat,location.lon,userId],(err,rows)=>
            {
                connection.release();
                if(!err){
                    logger.locationLog(userId,location);
                    callback(null); //success
                }else{
                    callback(err);  //failed
                }
            });
        }else{
            callback(err);          //failed
        }
    });
}
function updateListTradeLocation(tradeList,callback) //해당 tradeList리스트에 있는 row들의 드라이버 위치, 거래상태를 가져오는 함수
{
    var queryString = 'select T.t_num as tradeNum, T.t_state as tradeStatus, UL.ul_gps_lat as lat, UL.ul_gps_lon as lon from trade as T, userlocation as UL where T.t_driver_id = UL.ul_id and  (';
    for(var i = 0 ; i <tradeList.length; i++){
        if(i==0){
            queryString += 'T.t_num ='+tradeList[i];
        }else{
            queryString += ' or T.t_num ='+tradeList[i];
        }
    }
    queryString += ');';
    console.log(queryString);
    pool.getConnection((err,connection)=>
    {
        if(!err){
            connection.query(queryString,(err,rows)=>
            {
                connection.release();
                if(!err){
                    callback(null,rows);
                }else{
                    callback(err,null);
                }
            });
        }else{
            callback(err,null);
        }
    });


    //이후 드라이버들의 목록과 거래상태가 뜨는데 그걸 바로 다시 클라로 전송한다.
    //클라는 이후 받아서 tradeState 가 5가 된건 목록에서 제거하고 나머지는 다 위치 업데이트를 시켜준다 

}

module.exports.updateMyLocatation = function(userId,location,callback) //내 위치를 업데이트하는 작업
{
    console.log('userId '+userId);
    console.log('lat '+location.lat);
    console.log('lat '+location.lon);
    checkUserGotUL(userId,(errNoULinfo)=> 
    {//검사작업
        if(errNoULinfo){
            createUserULInfo(userId,(errCreateUL)=>
            {//생성작업
                if(errCreateUL){
                    callback(errCreateUL);//failed
                    return;
                }
            });
        }

        addUserULInfo(userId,location,(errAdd)=>
        {//추가작업
            if(errAdd){
                callback(errAdd);//failed
            }else{
                callback(null); //success
            }
            return;
        });
    });
};

module.exports.updateListTradeLocation = function(tradeList,callback) //해당거래들의 드라이버 위치,상태번호 출력
{
    updateListTradeLocation(tradeList,(err,result)=>
    {
        if(err){
            callback(err, null);    
        }else{
            callback(null, result);
        }
    });
};