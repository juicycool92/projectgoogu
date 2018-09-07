var sql = require('mysql');
var SQLinfo = require('../config/db/db_info');
var bcrypt = require('bcrypt-nodejs');
var pool = sql.createPool(SQLinfo);
var format = require('date-format');
var fs = require('fs');
//var reg = require('../config/regExps.js');
//유효성검사 + 정보저장 + 암호화 + 복호화 를 구현하는 창.
module.exports.generateHash = function(userPw){
	return bcrypt.hashSync(userPw,bcrypt.genSaltSync(8),null);
	
};
module.exports.validPassword = function(reqPw,resPw){
	console.log(reqPw + " " + resPw);
	return bcrypt.compareSync(reqPw,resPw);
};
module.exports.signIn = function(values,callback){
	var queryString = 'insert into user(u_id,u_pw,u_name,u_birthday,u_address,u_email,u_phone,u_sex,u_new_date) values(?,?,?,?,?,?,?,?,?);';
	pool.getConnection(function(err,connection){
		if(err){
			callback(err,null);
		}else{
			 connection.query(queryString,[values[0],values[1],values[2],values[3]
			 ,values[4],values[5],values[6],values[7],
			 ,format.asString('yy:MM:dd:hh:mm:ss',new Date())]
			 ,function(err,rows){
				 connection.release();
				if(err){
					console.log('err');
					callback(err,null);
				}else{
					console.log(rows[0]);
					callback(null,values[0]);
				}
			 });
		}
	});
};
module.exports.signInForAndroid = (values,callback)=>
{
	var queryString = 'insert into user(u_id,u_pw,u_name,u_phone,u_new_date) values(?,?,?,?,now());';
	pool.getConnection(function(err,connection){
		if(err){
			callback(err,null);
		}else{
			connection.query(queryString,[values[0],values[1],values[2],values[3]],(err,rows)=>
			{
				connection.release();
				if(err){
					console.log('sign in android user->signinforandroid funct err' +err);
					callback(err,null);
				}else{
					callback(null,values[0]);
				}
			});
		}
	});
};
module.exports.signInWithPicForAndroid = (values,callback)=>
{
	var queryString = 'insert into user(u_id,u_pw,u_name,u_phone,u_photo,u_th_photo,u_new_date) values(?,?,?,?,?,?,now());';
	pool.getConnection(function(err,connection){
		if(err){
			

			// if(reg.dupEntryChecker(err)){
			// 	console.log('정규식 쿼리 중복 엔트리');
			// }
			// else{
			// 	console.log('정규식 실패');
			// }
			// var objEroDefine = new RegExp('/(ER_DUP_ENTRY:)+/',['g']);
			// if(objEroDefine.check(err)){
			// 	console.log('정규식 쿼리 중복 엔트리');
			// }else{
			// 	console.log('정규식 실패');
			// }
				
			callback(err,null);
		}else{
			connection.query(queryString,[values[0],values[1],values[2],values[3],values[4],values[5]],(err,rows)=>
			{
				connection.release();
				if(err){
					console.log('sign in android user->signinforandroid funct err' +err);
					callback(err,null);
				}else{
					callback(null,values[0]);
				}
			});
		}
	});
};
module.exports.findById = function(userid,callback){
	handle_findId(userid,function(err){
		if(err){
			callback(err,null);
		}else{
			callback(null,userid);
		}
	});
};

module.exports.findOne = function(jsonFile,callback){
	console.log('findone 함수 :',JSON.stringify(jsonFile));
	
	handle_userLogin(jsonFile.userId, jsonFile.userPw,function(err,resultsId,resultPw){
		console.log('findone , end resulting');
		if(!err){
			console.log('findone, find success');
			callback(null,resultsId,resultPw);

		}else if(err==='warn'){
			console.log('findone, find failed with wrong attemp');
			callback(err,null,null);
		}else{
			console.log('findone, find failed');
			callback(err,null,null);
		}
	});
};
module.exports.requestMyImage = function(userId,callback){
	handle_findId(userId,function(err,resultUserId){
		if(!err || resultUserId===userId){
			requestUserImage(userId,function(err2,data){
				if(!err2 || data!==null){
					callback(null,data);
				}else if(!err || data === null){
					callback('err no Image',null);
				}else{
					callback(err2,null);
				}
			});
		}else if(!err || resultUserId !== userId){
			callback('err user not exist',null);
		}else{
			callback(err,null);
		}
	});
};
module.exports.updateImagesForUser = function(file,th_file,userId,callback){
	updateImagesForUser(file,th_file,userId,function(err){
		if(!err){
			console.log('에러없찐');
			callback(null);
		}
			
		else{
			console.log('에러있다');
			callback(err);
		}
			
	});
};
module.exports.getImageForTest = function(userId,callback){
	getImageForTest(userId,function(err,data){
		if(!err || data !== null){
			callback(null,data);
		}else{
			callback(err,null);
		}
	});
};
function requestUserImage(userId,callback){
	var queryString = 'select u_photo from user where u_id = ?';
	pool.getConnection(function(err, connection){
		if(err){
			console.log('sql연결 샐패!');
			callback(err,null);
		}else{
			connection.query(queryString,[userId],function(err,rows){
				connection.release();
				if(!err || rows[0]!==null){
					console.log('이미지추가, row가 있음!, 추가성공');
					callback(null,rows[0].u_photo);
				}else{
					console.log(err,null);
				}
			});
		}
	});	
}
function updateImagesForUser(file,th_file,userId,callback){
	var queryString = 'update user set u_photo=?, u_th_photo=? where u_id=?';
	pool.getConnection(function(err, connection){
		if(err){
			console.log('sql연결 샐패!');
			callback(err);
		}else{
			connection.query(queryString,[file,th_file,userId],function(err,rows){
				connection.release();
				if(err){
					console.log('이미지추가, 실패 sql오류');
					callback(err);
				}else{
					console.log('이미지추가, row가 있음!, 추가성공');
					callback(null);
				}
			});
		}
	});
}
function isDriver(userId,callback) {
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
					console.log('드라이버여부, row가 비었음, 추가가능');
					callback(null);
				}else{
					console.log('드라이버여부, row가 있음!, 추가불가');
					callback("err");
				}
			});
		}
	});
}
function checkDriverLoc(userId,callback){
	var queryString = 'select ul_lat as lat, ul_lot as lon from userlocation where ul_id = ? ;';
	pool.getConnection(function(err,connection){
		if(err){
			console.log('access failed, abondon all');
			callback(err);
		}else{
			connection.query(queryString,[userId],function(err,rows){
				connection.release();
				if(err){
					console.log('access success, but sql failed');
					console.log(queryString,[userId]);
					callback(err);
				}else{
					console.log('access success ,and query send susscess');
					callback(null);
				}
			});
		}
	});
}
function createDriverLoc(userId){
	var queryString = 'insert into userlocation (ul_id) values (?);';
	pool.getConnection(function(err,connection){
		if(err){
			console.log('access failed, abondon all');
		}else{
			connection.query(queryString,[userId],function(err,rows){
				connection.release();
				if(err){
					console.log('access success, but sql failed');
					console.log(queryString,[userId]);
				}else{
					console.log('access success ,and query send susscess');
				}
			});
		}
	});
}
function promoteUser(userId,callback){
	var queryString = 'insert into driver (d_id) values(?);';
	pool.getConnection(function(err,connection){
		if(err){
			console.log('access failed, abondon all');
			callback(err,null);
		}else{
			connection.query(queryString,[userId],function(err,rows){
				connection.release();
				if(err){
					console.log('access success, but sql failed');
					console.log(queryString,[userId]);
					callback(err,null);
				}else{
					console.log('access success ,and query send susscess');
					callback(null,rows);
				}
			});
		}
	});
}
module.exports.setPromoteDriver = function(userId,callback){
	console.log('hello fucking world');
	isDriver(userId, function(err){
		if(err){
			callback(err);
		}else{
			promoteUser(userId,function(err){
				if(!err){
					callback(null);
					checkDriverLoc(userId,function(err){
						if(err){
							createDriverLoc(userId);
						}
					});
				}else{
					callback(err);
				}
			});	
		}
	});
};
	
/*
module.exports.checkLoginAndGetId = function(req){
	if(req.authenticated()){
		return ({'login':true,'userId':req.user});
	}
	return ({'login':false,'userId':'guest'});
};
*/
function handle_userLogin(reqid,reqpw,callback){
	console.log('핸들러 펑션 도달');
	var queryString = 'select u_id,u_pw from user where u_id = ?';
	pool.getConnection(function(err, connection) {
		if(err){
			console.log('enter if');
			callback('error','connectionfailed');
			//return {status : "error", info : "connection failed"};
		}else{
			console.log('enter else');
			connection.query(queryString,[reqid],function(err,rows){
				connection.release();
				if(err){
					console.log('enter else,if');
					callback('error','serverside error',null);
					//return (err,{status: "error", info : "autorization faild, serverside"});
				}else if(!rows[0]){
					console.log('enter else,else if 1');
					callback('error','serverside error',null);
				}else if(rows[0].mem_id !== ''){
					console.log('enter else,else if 2');
					console.log(rows[0].u_id+rows[0].u_pw);
					
					callback(null,rows[0].u_id,rows[0].u_pw);
					
					//return  (err,{status:"success", info:rows[0].mem_id});
				}else{
					console.log('enter else,else');
					callback('warn','wrong id',null,null);
					//return (null,{status: "warn", info : "wrong id or pw"});
				}
			});
		}
	});
}
/*
module.exports.getUserInfo = function(userId,callback){
	//userinfo 호출
	//driverinfo 호출
	//결과 전송
	//이부분은 우선순위가 아니니  다음에 하기로 하자ww
};
*/
function handle_findId(requestId,callback){// id 검색함수 리턴값은 err,결과. 결과가 입력값과 동일한가를 따지자.
	var stringQuery = 'select u_id from user where u_id = ?';
	pool.getConnection(function(err,connection){
		if(err){
			callback(err);
		}else{
			connection.query(stringQuery,[requestId],function(err,rows){
				connection.release();
				if(err){
					callback(err);
				}else if(!rows){
					callback(err);
				}else if(rows[0].u_id === requestId){
					callback(null);
				}else{
					callback(err);
				}
			});
		}
	});
}