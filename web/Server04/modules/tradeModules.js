 var sql = require('mysql');
var SQLinfo = require('../config/db/db_info');
var pool = sql.createPool(SQLinfo);
var format = require('date-format');

function setRequestInboundYes(tradeNum,callback){
	var queryString = 'update trade set t_state = 2, t_updateTime = now() where t_num = ?;';

	pool.getConnection(function(err, connection){	
		if(!err){
			connection.query(queryString,[tradeNum],function(err,rows){
				connection.release();
				if(!err || rows.changedRows!==0){
					callback(null);
				}else{
					callback(err);
				}
			});
		}else{
			callback(err);
		}
	});
}

function setRequestInboundNo(tradeNum,callback){
	var queryString = 'update trade set t_state = 0, t_driver_id = null, t_driver_name = null, t_driver_phone = null, t_updateTime = now() where t_num = ?;';

	pool.getConnection(function(err, connection){	
		if(!err){
			connection.query(queryString,[tradeNum],function(err,rows){
				connection.release();
				if(!err || rows.changedRows!==0){
					callback(null);
				}else{
					callback(err);
				}
			});
		}else{
			callback(err);
		}
	});
}

function getDriverInfoFromMyTrade(tradeNum,callback){
	var queryString = 'select U.u_name as driverName, D.d_id as driverId, D.d_rank as driverGrade, D.d_memo as driverMemo, D.d_photo as driverPhoto,T.t_num as tradeNum, T.t_goods_name as tradeName from driver as D, trade as T ,user as U where D.d_id= ( select t_driver_id from trade where t_num = ?) and T.t_num = ? and U.u_id=D.d_id;';
	pool.getConnection(function(err,connection){
		if(!err){
			connection.query(queryString,[tradeNum,tradeNum],function(err,rows){
				connection.release();
				if(!err || rows[0]!==null){
					callback(null,rows[0]);
				}else{
					callback(err,null);
				}
			});
		}
	});
}

function getMySelectedTrade(tradeNum,callback){
	var queryString ='select t_num as tradeNum, t_state as tradeState, t_updateTime as tradeUpdateTime, t_goods_name as tradeName, t_cost as tradeCost, t_quantity as tradeWeight, t_size as tradeSize, t_notice as tradeMemo, t_sender_name as tradeSenderName, t_departure_place as tradeSenderLoc ,t_departure_time as tradeSenderTime,t_receiver_name as tradeReceiverName, t_receiver_phone as tradeReceiverPhone, t_arrival_place as tradeReceiverLoc, t_driver_name as tradeDriverName from trade where t_num = ?;';
	pool.getConnection(function(err,connection){
		if(!err){
			connection.query(queryString,[tradeNum],function(err,rows){
				connection.release();
				if(!err || rows[0] !== null){
					callback(null,rows[0]);
				}else{
					callback(err,null);
				}
			});
		}else{
			callback(err,null);
		}
	});
}

function getOthersSelectedTrade(tradeNum,callback){
	var queryString ='select t_num as tradeNum, t_state as tradeState, t_updateTime as tradeUpdateTime, t_goods_name as tradeName, t_cost as tradeCost, t_quantity as tradeWeight, t_size as tradeSize, t_notice as tradeMemo, t_sender_name as tradeSenderName, t_departure_place as tradeSenderLoc ,t_departure_time as tradeSenderTime,t_receiver_name as tradeReceiverName, t_receiver_phone as tradeReceiverPhone, t_arrival_place as tradeReceiverLoc from trade where t_num = ?;';
	pool.getConnection(function(err,connection){
		if(!err){
			connection.query(queryString,[tradeNum],function(err,rows){
				connection.release();
				if(!err || rows[0] !== null){
					callback(null,rows[0]);
				}else{
					callback(err,null);
				}
			});
		}else{
			callback(err,null);
		}
	});
}

function getAllIdOfTradeByNum(tradeNum,username,callback){
	var queryString ='select t_sender_id , t_driver_id from trade where t_num = ?';
	pool.getConnection(function(err, connection){
		if(!err){
			connection.query(queryString,[tradeNum],function(err,rows){
				connection.release();
				if(!err || rows[0] !== null){
					callback(null,rows[0]);
				}else{
					callback(err,null);
				}
			});
		}else{
			callback(err,null);
		}
	});
}

function searchForMyTradeList(userId,listNum,callback){
	console.log('visite search4MyTradeList');
	var listPerPage = 4; //페이지당 노출되는 건수이며, 나중에 가능하다면 상수화? 시키든가 해서 바뀔수 없게 하는게 올바른 행위일 것 이다.
	var queryString ='select t_num, t_goods_name, t_state from trade where t_sender_id = ? or  t_driver_id = ? order by t_num desc;';
	pool.getConnection(function(err,connection){
		if(err){
			console.log('searchformytrade 1');
			callback(err,null);
		}else{
			connection.query(queryString,[userId,userId],function(err,rows){
				connection.release();
				if(err){
					console.log('searchformytrade 2');
					callback(err,null,null,null);
				}else if(!rows[0]){
					console.log('searchformytrade 3');
					callback(null,null,null,null);
				}else{
					console.log(JSON.stringify(rows)+userId);
					console.log('searchformytrade 4');
					var result = [];
					for(var i = listNum*listPerPage ; i < listNum*listPerPage+listPerPage ; i ++){
						console.log('looping['+i+']');
						if(i===rows.length){
							console.log(rows.length);
							break;
						}else if(!rows[i] || rows[i]===null || i > rows.length){
							callback('empty',null,null,null);
							return;
						}else{
							result.push(rows[i]);
						}
					}
					//console.log('sibal : '+result.pop());
					callback(null,result,listPerPage,rows.length);
				}
			});
		}
	});		
}

// function getTradeDepTime(tradeNumlist,userName,callback)
// {
// 	var queryString = 'select t_num as tradeNum, UNIX_TIMESTAMP(t_departure_time) as tradeDepTime from trade where t_sender_id = ? and t_state = 4 and';
// 	for(var i = 0 ; i < tradeNumlist.length-1 ; i ++){
// 		queryString += 't_num = '+tradeNumlist[i]+' and ';
// 	}
// 	queryString += 't_num = '+tradeNumlist[tradeNumlist.length-1] + ' ;';
// 	pool.getConnection((err,connection)=>
// 	{
// 		if(!err){
// 			connection.query(queryString,[userName],(err2,rows)=>
// 			{
// 				connection.release();
// 				if(!err2 || rows[0]!==null){
// 					callback(null,rows[0]);
// 				}else{
// 					callback(err2,null);
// 				}
// 			});
// 		}else{
// 			callback(err,null);
// 		}
// 	});
// }

function getTradeDepTime(tradeNum,userName,callback)
{
	var queryString = 'select t_num as tradeNum, UNIX_TIMESTAMP(t_departure_time) as tradeDepTime from trade where t_num = ? and t_sender_id = ? and t_state = 4;';
	pool.getConnection((err,connection)=>
	{
		if(!err){
			connection.query(queryString,[tradeNum,userName],(err2,rows)=>
			{
				connection.release();
				if(!err2 || rows[0]!==null){
					callback(null,rows[0]);
				}else{
					callback(err2,null);
				}
			});
		}else{
			callback(err,null);
		}
	});
}
function getTradeDepListTime(tradeNumList,userName,callback)
{
	console.log('tradeNumList is '+tradeNumList);
	console.log('tradeNumList stringify is '+JSON.stringify(tradeNumList));
	console.log('tradeNumList size is '+tradeNumList.length);
	console.log('tradeNumList[0] is '+tradeNumList[0]);
	var queryString = 'select t_num as tradeNum, UNIX_TIMESTAMP(t_departure_time) as tradeDepTime from trade where t_sender_id = ? and t_state = 4 and( ';
	for(var i = 0 ; i < tradeNumList.length-1 ; i ++){
		queryString += 't_num = '+tradeNumList[i]+' or ';
	}
	queryString += 't_num = '+tradeNumList[tradeNumList.length-1] + ');';
	console.log('query : '+queryString);
	pool.getConnection((err,connection)=>
	{
		if(!err){
			connection.query(queryString,[userName],(err2,rows)=>
			{
				connection.release();
				if(!err2 || rows[0]!==null){
					callback(null,rows);
				}else{
					callback(err2,null);
				}
			});
		}else{
			callback(err,null);
		}
	});
}

module.exports.getTradeDepTime = function(tradeNum,userName,callback)
{
	getTradeDepTime(tradeNum,userName,(err,result)=>
	{
		if(!err || result !== null){
			callback(null,result);
		}else{
			callback(err,null);
		}
	});
};

module.exports.getTradeListDepTime = function(tradeNumList,userName,callback)
{
	getTradeDepListTime(tradeNumList,userName,(err,result)=>
	{
		console.log('hai? '+tradeNumList);
		if(!err || result !== null){
			callback(null,result);
		}else{
			callback(err,null);
		}
	});
};

module.exports.setRequestInboundAccept = function(tradeNum,userName,callback){
	setRequestInboundYes(tradeNum,userName,function(err){
		if(!err || err === null){
			callback(null);
		}else{
			callback(err);
		}
	});
};
module.exports.setRequestInboundDecline = function(tradeNum,callback){
	setRequestInboundNo(tradeNum,function(err){
		if(!err || err === null){
			callback(null);
		}else{
			callback(err);
		}
	});
};

module.exports.getDriverInfoWhoRequest = function(tradeNum,callback){
	getDriverInfoFromMyTrade(tradeNum,function(err,result){
		if(!err || result!== null){
			callback(null,result);
		}else{
			callback(err,null);
		}
	});
};//본인의 거래내역에 ㅇ비찰을 건 드라이버의 정보를 가져오는 함수

module.exports.getTradeAsOwner = function(tradeNum,callback){
	//모두+드라이버정보
	getMySelectedTrade(tradeNum,function(err,result){
		if(!err || result !== null){
			callback(null,result);
		}else{
			callback(err,null);
		}
	});
};//본인의 거래내역을 호출한다, 모든 정보를 보내준다

module.exports.getTradeAsDriver = function(tradeNum,callback){
	//재외, senderid,driver추가정보
	getOthersSelectedTrade(tradeNum,function(err,result){
		if(!err || result !== null){
			callback(null,result);
		}else{
			callback(err,null);
		}
	});
};//드라이버입장의 거래내역을 호출한다, 제한된 정보를 보내준다.

module.exports.getUserPosTrade = function(tradeNum,username,callback){
	//거래 검색, 나온 row를 비교해서 누구인가를 비교하자
	//select senderid , driverid from trade where tradenum = ?;
	getAllIdOfTradeByNum(tradeNum,username,function(err,result){
		if(!err || result!==null){
			if(result.t_sender_id === username){
				callback(null,'sender');
			}else if(result.t_driver_id === username){
				callback(null,'driver');
			}else{
				callback('error',null); // 오류인 경우, 출력할게 없다
			}
		}else{
			callback(err,null); // 타인의 거래내역일 경우, 출력을 금한다.
		}
	});
};//해당 거래가 내가 주인인가, 드라이버 인가, 틀렸는가를 확인하는 함수


module.exports.getModuleByNum = function(status,tradeNum,callback){
	var queryString = 'select t_goods_name, t_cost, t_size, t_quantity, t_notice, t_sender_name, t_departure_place_lat, t_departure_place_lon, t_departure_time, t_arrival_place_lat, t_arrival_place_lon, t_arrival_time from trade where t_state=? and t_num = ?;';
	pool.getConnection(function(err,connection){
		if(!err){
			connection.query(queryString,[status,tradeNum],function(err,rows){
				connection.release();
				if(err){
					console.log('하이고오 sql 틀맀다 자슥아');
					callback(err,null);
				}else if(!rows[0]){
					console.log('하이고오 틀맀다 자슥아');
					callback(err,null);
				}else{
					console.log('결과나왔다 자슥아');
					callback(null,rows[0]);
				}
			});
		}else{
			callback(err,null);
		}
	});
};

module.exports.getTradeStatus = function(tradeNum,callback){
	var queryString = 'select t_state from trade where t_num = ?';
	pool.getConnection(function(err,connection){
		if(!err){
			connection.query(queryString,[tradeNum],function(err,rows){
				connection.release();
				if(!err){
					callback(null,rows[0].t_state);
				}else{
					callback(err,null);
				}
			});
		}else{
			callback(err,null);
		}
	});
};
module.exports.driverRequestTrade = function(req,callback){
	var queryString = 'update trade set t_driver_id = ?, t_driver_name = (select u_name from user where u_id = ?), t_driver_phone = (select u_phone from user where u_id = ?), t_state=1, t_updateTime = now() where t_num=?;';
	pool.getConnection(function(err,connection){
		if(!err){
			connection.query(queryString,[req.body.driverId,req.body.driverId,req.body.driverId,req.body.tradeNum],function(err,rows){
				connection.release();
				if(!err){
					callback(null,rows[0]);
				}else{
					callback(err,null);
				}
			});
		}else{
			callback(err,null);
		}
	});
		
};
module.exports.uploadTrade = function(req,userId,callback){
	console.log(req.body.product_id+':'+req.body.product_send_location+':'+req.body.product_send_lat+':'+req.body.product_send_lon+':'+req.body.product_send_time+':'+req.body.product_receiver_location+':'+req.body.product_receiver_lat+':'+req.body.product_receiver_lon+':'+req.body.product_receiver_time+':'+req.usernameField+':'+req.body.product_sender_Name+':'+req.body.product_sender_Phone+':'+req.body.product_receiver_name+':'+req.body.product_receiver_Phone+':'+req.body.product_cost+':'+req.body.product_size+':'+req.body.product_weight+':'+req.body.product_memo);
	var queryString = 'insert into trade (t_goods_name,t_departure_place,t_departure_place_lat,t_departure_place_lon,t_departure_time,t_arrival_place,t_arrival_place_lat,t_arrival_place_lon,t_arrival_time,t_sender_id,t_sender_name,t_sender_phone,t_receiver_name,t_receiver_phone,t_cost,t_size,t_quantity,t_notice,t_updateTime) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now());';
	pool.getConnection(function(err,connection){
		if(!err){
			connection.query(queryString,[req.body.product_id,req.body.product_send_location +' '+ req.body.product_send_location2,req.body.product_send_lat,req.body.product_send_lon,req.body.product_send_time,req.body.product_receiver_location+' '+req.body.product_receiver_location2,req.body.product_receiver_lat,req.body.product_receiver_lon,req.body.product_receiver_time,userId,req.body.product_sender_Name,req.body.product_sender_Phone,req.body.product_receiver_name,req.body.product_receiver_Phone,req.body.product_cost,req.body.product_size,req.body.product_weight,req.body.product_memo],function(err,rows){
				connection.release();
				if(!err){
					callback(null,rows[0]);
				}else{
					callback(err,null);
				}
			});
		}else{
			callback(err,null);
		}
	});
};
module.exports.isDriver = function(userId,callback){
	var queryString = 'select d_id from driver where d_id = ?;';
	pool.getConnection(function(err, connection){
		if(err){
			console.log('드라이버여부, sql연결 샐패!');
			callback(err);
		}else{
			connection.query(queryString,[userId],function(err,rows){
				connection.release();
				if(err){
					console.log('드라이버여부, 실패 sql오류');
					callback(err);
				}else if(!rows[0]){
					console.log('드라이버여부, row가 비었음');
					callback("empty");
				}else{
					console.log('드라이버여부, row가 있음!');
					callback(null);
				}
			});
		}
	});
};
module.exports.getMyTradeList = function(userId, listNum, callback){
	console.log('visite trade.getMyTradeList');
	searchForMyTradeList(userId,listNum,function(err,rows,listPerPage,totalSize){
		console.log('done visite trade.getMyTradeList');
		if(!err || err === null || rows!== null){
			callback(null,rows,listPerPage,totalSize);
		}else{
			callback(err,null,null,null);
		}
	});
};

module.exports.getTradeStatus = function(tradeNum,callback){
	getTradeStatus(tradeNum,function(err,result){
		if(err || !result || result===null){
			callback('error',null);
		}else{
			callback(null,result);
		}
	});
};//특정거래의 상태번호를 리턴한다.
function getTradeStatus(tradeNum,callback){
	var queryString = 'select t_state from trade where t_num = ?';
	pool.getConnection(function(err,connection){
		if(!err){
			connection.query(queryString,[tradeNum],function(err,rows){
				connection.release();
				if(!err || rows[0] !== null){
					callback(null,rows[0].t_state);
				}else{
					callback(err,null);
				}
			});
		}else{
			callback(err,null);
		}
	});
}