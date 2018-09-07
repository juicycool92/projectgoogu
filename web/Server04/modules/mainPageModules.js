var sql = require('mysql');
var SQLinfo = require('../config/db/db_info');
var logModule = require('./logModules.js');
var locationModule = require('./locationModules.js');
var pool = sql.createPool(SQLinfo);

function searchForMyTradeDriver(userId,callback){
	var queryString ='select UL.ul_gps_lat,UL.ul_gps_lon, UL.ul_id, T.t_num, T.t_goods_name from userlocation UL,trade T where T.t_driver_id = UL.ul_id and T.t_sender_id = ? and T.t_state <= 2;';
	pool.getConnection(function(err,connection){
		if(err){
			callback(err,null);
		}else{
			connection.query(queryString,[userId],function(err,rows){
				connection.release();
				if(err){
					callback(err,null);
				}else if(!rows[0]){
					callback(null,null);
				}else{
					callback(null,rows);
				}
			});
		}
	});		

}
function searchForMyTradeList(userId,callback){
	var queryString = 'select t_num,t_goods_name,t_state from trade where t_sender_id = ? order by t_updateTime desc;';
	pool.getConnection(function(err,connection){
		if(err){
			callback(err,null);
		}else{
			connection.query(queryString,[userId,userId],function(err,rows){
				connection.release();
				if(err){
					callback(err,null);
				}else if(!rows[0]){
					callback(null,null);
				}else{
					callback(null,rows);
				}
			});
		}
	});		
}
function searchForTradeList(locations,callback){
	console.log('mainPageModule.searchForTradeList locations['+locations.latResult_01);
	var queryString = 'select t_departure_place_lat, t_departure_place_lon, t_num, t_goods_name,t_cost, t_departure_time, t_arrival_place from trade where t_departure_place_lat > ? and t_departure_place_lat < ? and t_departure_place_lon > ? and t_departure_place_lon < ? and t_state = 0;';
	pool.getConnection(function(err,connection){
		if(err){
			callback(err,null);
		}else{
			connection.query(queryString,[locations.latResult_01,locations.latResult_02,locations.lonResult_01,locations.lonResult_02],function(err,rows,field){
				connection.release();
				if(err){
					callback(err,null);
				}else if(!rows[0]){
					callback(err,null);
				}else{
					callback(null,rows);
				}
			});
		}
	});
}
function calculate(lat,lon,zoom,callback){
	logModule.currentMap(lat, lon);
	var radious = 0;
	zoom = zoom.toString(); // web과 안드로이드에서 보내는 인자값이 미쳤어, 명시화 시키는작업, 불필요하지만(자동으로 해주기에) 낭비나 문제는 아닌 코드
	
	switch(zoom){
	case '1' : radious=500 ;break;
	case '2' : radious=500 ;break;
	case '3' : radious=500 ;break;
	case '4' : radious=5000 ;break;
	case '5' : radious=5000 ;break;
	case '6' : radious=5000 ;break;
	case '7' : radious=5000 ;break;
	case '8' : radious=5000 ;break;
	case '9' : radious=5000 ;break;
	case '10' : radious=5000 ;break;
	case '11' : radious=5000 ;break;
	default : console.log('err : radious out range');return(null); //zoomlevel을 우선은 5까지만 제한하도록 해 본다.
	}
	//준비단계
	var latHour_00 = 0,latMinuate_00 = 0,latSecond_00 = 0;
	latHour_00 = parseInt(lat);
	var result = lat - latHour_00 ;
	latMinuate_00 = parseInt((result * 60));
	result = (( result * 60 )% latMinuate_00)*60 ;
	latSecond_00 = result.toFixed(1)/1;
	
	var lonHour_00 = 0,lonMinuate_00 = 0,lonSecond_00 = 0;
	lonHour_00 = parseInt(lon);
	result = lon - lonHour_00 ;
	lonMinuate_00 = parseInt((result * 60));
	result = (( result * 60 )% lonMinuate_00)*60 ;
	lonSecond_00 = result.toFixed(1)/1;
	//디코딩단계, 이로써 요청좌표 lat lon의 실측 값으로 나타냈음.
	
	var tmpVar1 = radious / 30;
	
	var latHour_01 = latHour_00,
		latMinuate_01 = latMinuate_00,
		latSecond_01 = latSecond_00 - tmpVar1 ;
	
	while(latSecond_01 <= 0 || latSecond_01 > 60){
		latMinuate_01--;
		latSecond_01+=60;
	}while(latMinuate_01 <= 0 || latMinuate_01 > 60){
		latHour_01--;
		latMinuate_01 += 60;
	}
	
	latSecond_01 = latSecond_01.toFixed(1)/1;
	//lat - 의 경우
	

	var latHour_02 = latHour_00,
	latMinuate_02 = latMinuate_00,
	latSecond_02 = latSecond_00 + tmpVar1;
	
	while(latSecond_02 <= 0 || latSecond_02 > 60){
		latMinuate_02++;
		latSecond_02-=60;
	}while(latMinuate_02 <= 0 || latMinuate_02 > 60){
		latHour_02++;
		latMinuate_02 -= 60;
	}
	
	latSecond_02 = latSecond_02.toFixed(1)/1;
	//lat + 의 경우
	
	
	var tmpVar2 = radious / 30;
	
	var lonHour_01 = lonHour_00,
		lonMinuate_01 = lonMinuate_00,
		lonSecond_01 = lonSecond_00 - tmpVar2 ;
	
	while(lonSecond_01 <= 0 || lonSecond_01 > 60){
		lonMinuate_01--;
		lonSecond_01+=60;
	}while(lonMinuate_01 <= 0 || lonMinuate_01 > 60){
		lonHour_01--;
		lonMinuate_01 += 60;
	}
	
	lonSecond_01 = lonSecond_01.toFixed(1)/1;
	//lon - 의 경우
	
	var lonHour_02 = lonHour_00,
	lonMinuate_02 = lonMinuate_00,
	lonSecond_02 = lonSecond_00 + tmpVar2;
	
	while(lonSecond_02 <= 0 || lonSecond_02 > 60){
		lonMinuate_02++;
		lonSecond_02-=60;
	}while(lonMinuate_02 <= 0 || lonMinuate_02 > 60){
		lonHour_02++;
		lonMinuate_02 -= 60;
	}
	
	lonSecond_02 = lonSecond_02.toFixed(1)/1;
	//lon + 의 경우

	var latResult_00 = (((latSecond_00/60)+latMinuate_00)/60)+latHour_00;
	var latResult_01 = (((latSecond_01/60)+latMinuate_01)/60)+latHour_01;
	var latResult_02 = (((latSecond_02/60)+latMinuate_02)/60)+latHour_02;
	
	var lonResult_00 = (((lonSecond_00/60)+lonMinuate_00)/60)+lonHour_00;
	var lonResult_01 = (((lonSecond_01/60)+lonMinuate_01)/60)+lonHour_01;
	var lonResult_02 = (((lonSecond_02/60)+lonMinuate_02)/60)+lonHour_02;
	//
	console.log(latResult_00+' : '+lonResult_00);
	console.log(latResult_02+' : '+lonResult_02);
	
	//
	
	//인코딩단계
	console.log('return val '+latResult_01+'/'+lonResult_01+'/'+latResult_02+'/'+lonResult_02);
	return ({latResult_01,lonResult_01,latResult_02,lonResult_02});
	//결과리턴
}

module.exports.onLoadMyTradeDriver = function(userId,callback){
	searchForMyTradeDriver(userId,function(err,result){
		if(err){
			callback(err,null);
		}else if(result===null){
			callback(null,null);
		}else{
			//console.log('line 13 in mainModule, resuit :'+JSON.stringify(result[0]))
			callback(null,result);
		}
	});
};
module.exports.onLoadMyTradeList = function(userId,callback){
	searchForMyTradeList(userId, function(err,result){
		if(err){
			callback(err,null);
		}else if(result===null){
			callback(null,null);
		}else{
			callback(null,result);
		}
	});
};

module.exports.onLoadAllTradeList = function(lat,lon,zoom,userId,callback){
	if(lat===null || lon===null || zoom===null){
		callback('err',null);
		return;
	}
	if(userId!=='guest'){
		console.log('야이시발');
		locationModule.updateMyLocatation(userId,{'lat':lat,'lon':lon},(err)=>
		{
			if(err){
				console.log(userId+' : update location failed ::'+err);
			}
		});
	}
	var	results = calculate(lat, lon, zoom);	
	searchForTradeList(results,function(err,rows){
		if(!err){
			callback(null,rows);
		}else{
			callback(err,null);
		}
	});
};

module.exports.isDriver = function(userId,callback){
	var queryString = 'select d_id from driver where d_id = ?;';
	pool.getConnection(function(err, connection){
		if(err){
			callback(err);
		}else{
			connection.query(queryString,[userId],function(err,rows){
				connection.release();
				if(err){
					callback(err);
				}else if(!rows[0]){
					callback("empty");
				}else{
					callback(null);
				}
			});
		}
	});
};
module.exports.updateDriverLocation = function(userId,lat,lon){
	var queryString = 'update USERlocation set ul_gps_lat = ? , ul_gps_lon= ? where ul_id = ?;';
	pool.getConnection(function(err, connection){
		if(!err){
			connection.query(queryString,[lat,lon,userId],function(err,rows){
				connection.release();
				if(err){
					console.log('updateUserLocation failed');
				}else{
					console.log('update location into userLocation success');
				}
			});
		}
	});
};
module.exports.onLoadMyTradeDriverList = function(userId,callback){
	var queryString = 'select T.t_num as tradeNum, T.t_driver_id as driverId, U.u_th_photo as driver_t_profile, UL.ul_gps_lat as driverLat, UL.ul_gps_lon as driverLon, T.t_state as tradeStatus from trade as T, user as U, userlocation as UL where T.t_sender_id = ? and (T.t_state >= 4 and T.t_state <= 5) and U.u_id = T.t_driver_id and U.u_id=UL.ul_id;';
	pool.getConnection(function(err,con){
		if(err){
			callback(err,null);
		}else{
			con.query(queryString,[userId],(err,rows)=>{
				con.release();
				if(err){
					console.log('sql err :'+err);
					callback(err,null);
				}else if(!rows){
					callback('err',null);
				}else{
					sortMyTradeDriverList(rows,(err2,result)=>{
						callback(null,result);
					});
				}
			});
		}
	});
}
function sortMyTradeDriverList(rows,callback){
	var lists = [];
	if(rows.length==0){
		callback('err',null);
		return;
	}
	for(var i in rows){
		if(lists.length===0){
			lists.push(rows[i]);
			continue;
		}
		for(var j = 0 ; j < lists.length ; j ++){
			if(lists[j]===rows[i]){
				break;
			}else if(j===lists.length-1){
				lists.push(rows[i]);
			}
			console.log('j :'+j);
			console.log(' and j==lists.length-1'+j===lists.length-1);
		}
		// if(!lists.find(rows[i])){
		// 	console.log('추가중임');
		// 	lists.push(rows[i]);
		// 	console.log('추가완료 값은 '+lists[i]);
		// };
	}
	callback(null,lists);
}
module.exports.onLoadMyActiveTrade = function(userId,callback) {
	var queryString = 'select t_num,t_state,t_departure_time from trade where t_sender_id = ? and t_state>0 and t_state<3;';
	pool.getConnection(function(err, connection){
		if(err){
			callback(err);
		}else{
			connection.query(queryString,[userId],function(err,rows){
				connection.release();
				if(err){
					callback(err,null);
				}else if(!rows[0]){
					callback(null,null);
				}else{
					callback(null,rows);
				}
			});
		}
	});
};
module.exports.getTestFunction = function(tradeNum,callback){
	queryString = 'select t_updateTime from trade where t_num = ?;';
	pool.getConnection((err,connection)=>{
		if(!err){
			connection.query(queryString,[tradeNum],(err,rows)=>{
				connection.release();
				if(err)
					callback(err,null);
				else if(!rows)
					callback('emptyrows',null);
				else{
					callback(null,rows[0].t_updateTime);
				}
					
			});
		}else{
			callback(err,null);
		}
	});
};
