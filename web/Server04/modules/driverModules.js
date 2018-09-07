var sql = require('mysql');
var SQLinfo = require('../config/db/db_info');
var pool = sql.createPool(SQLinfo);
var format = require('date-format');

function isDriver(driverId,callback){
	var queryString = 'select d_id from driver wehre d_id = ?';
	pool.getConnection(function(err,connection){
		if(err){
			console.log('드라이버여부, sql연결 샐패!');
			callback(err);
		}else{
			connection.query(queryString,[driverId],function(err,rows){
				connection.release();
				if(err){
					callback(err);
				}else if(!rows[0] || rows[0] ===null){
					callback('not found');
				}else{
					callback(null);
				}
			});
		}
	});
}
function getDriverInfo(driverId,callback){
	var queryString = 'select d_id, d_rank, d_memo, d_photo from driver where d_id = ?';
	pool.getConnection(function(err,connection){
		if(err){
			console.log('드라이버여부, sql연결 샐패!');
			callback(err,null);
		}else{
			connection.query(queryString,[driverId],function(err,rows){
				connection.release();
				if(err){
					callback(err,null);
				}else if(!rows[0] || rows[0] ===null){
					callback('not found',null);
				}else{
					callback(null,rows[0]);
				}
			});
		}
	});
}
function getDriverInfoLite(driverId,callback){
	var queryString = 'select U.u_name as driverName, U.u_photo as driverPic, D.d_rank as driverRank, D.d_memo as driverContext from driver as D, user as U where  U.u_id = D.d_id and D.d_id = ?; ';
	pool.getConnection((err,connection)=>{
		if(err){
			callback(err,null);
		}else{
			connection.query(queryString,[driverId],(err,rows)=>{
				connection.release();
				if(err){
					callback(err,null);
				}else if(rows[0]===null){
					callback('rows are empty',null);
				}else{
					callback(null,rows);
				}

			});
		}
	});
}

module.exports.getDriverInfo = function(driverId,callback){
	isDriver(driverId,function(err){
		if(!err || err !==null){
			//드라이버가 맞는경우
			getDriverInfo(driverId,function(err,result){
				if(!err || err !==null){
					callback(null,result);
				}else{
					callback(err,null);
				}
			});
		}else{
			console.log('driver module : getDriverInfo : err : '+err);
			callback(err,null);
		}
	});
};
module.exports.getDriverInfoLite = (driverId,callback)=>{
	isDriver(driverId,(err)=>{
		if(!err || err !== null){
			getDriverInfoLite(driverId,(err,result)=>{
				if(!err || result!==null){
					callback(null,result[0]);
				}else{
					callback(err,null);
				}
			});
		}
	});
};