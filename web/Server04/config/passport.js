var LocalStrategy = require('passport-local').Strategy;
var User = require('../modules/user');
//아래 네개는 이미징 처리를 위한거//
var sharp = require('sharp');
/////////////////////////////////////
var reg = require('./regExps.js');

//User 구현(유효성검사,정보저장 펑션 구현할것)
module.exports = function(passport){
	passport.serializeUser(function(user,done){
		done(null,user);
	});
	
	passport.deserializeUser(function(id,done) {
		User.findById(id,function(err,user){
			done(err,user);
		});
	});
	passport.use('sign',new LocalStrategy({
		usernameField: 'userId',
		passwordField: 'userPw',		
		passReqToCallback: true
	}, function(request,userid,userpw,done){
		var requestValues = [];
		requestValues.push(userid);
		requestValues.push(User.generateHash(userpw));
		requestValues.push(request.body.userName);
		requestValues.push(request.body.userBirth);	
		requestValues.push(request.body.userAddress);
		requestValues.push(request.body.userMail);
		requestValues.push(request.body.userPhone);
		requestValues.push(request.body.userSex);
//		for(var i in requestValues){
//			console.log(requestValues[i]);
//		}
		User.signIn(requestValues,function(err,user){
			if(err){
				return done(err); 
			}else if(!user){
				return done(null,false,request.flash('signMessage','err'));
			}else {
				return done(null,user);
			}
		});
	}));
	passport.use('signForAndroid',new LocalStrategy({
		usernameField: 'userId',
		passwordField: 'userPw',		
		passReqToCallback: true
	},(request,userid,userpw,done)=>
	{
		sharp(request.file.buffer).resize(150,150).toBuffer()
		.then(th_file => 
			{
				var requestValues = [];
				requestValues.push(userid);
				requestValues.push(User.generateHash(userpw));
				requestValues.push(request.body.userName);
				requestValues.push(request.body.userPhone);
				requestValues.push(request.file.buffer);
				requestValues.push(th_file);
				User.signInWithPicForAndroid(requestValues,(err,user)=>
				{
					if(err){ 
						reg.dupEntryChecker(err,(result)=>
						{
							console.log('\n결과'+result);
							switch(result){
								case null : console.log('중복아님');break;
							case 1: console.log('아이디 중복');break;
							case 2: console.log('전화번호 중복');break;
							case 3: console.log('알려지지 않은 중복');break;
							default : console.log('case문조차 알수없는 문제 case값은 :'+result);break;
							}
						});
						
						// var regex = new RegExp('\(ER_DUP_ENTRY:)+','g');
						// if(regex.test(err)){
						// 	console.log('정규식 쿼리 중복 엔트리');
						// }else{
						// 	console.log('정규식 실패');
						// 	console.log(objEroDefine.test(err));
						// }
						return done(err); 
					}else if(!user){
						return done(null,false,request.flash('signMessage','err'));
					}else {
						return done(null,user);
					}
				});
			});
		}
	));
	
	passport.use('login',new LocalStrategy({
		usernameField: 'userId',
		passwordField: 'userPw',		
		passReqToCallback: true
	},function(req,userId,userPw,done){
		User.findOne({'userId':userId,'userPw':userPw},function(err,resultsId,resultPw){
			console.log('hello');
			if(err){
				console.log('passport entering if');
				//return done(err);
				return done(null,false,req.flash('loginMessage','cannotfindid'));
			}else if(!resultsId){
				console.log('passport entering else if');
				return done(null,false,req.flash('loginMessage','cannotfindid'));
			}else{
				//console.log('passport entering else : '+JSON.stringify(user['mem_pw']));

				if(User.validPassword(userPw, resultPw)){
					return done(null,resultsId);
				}else{
					return done(null,false,req.flash('loginMessage','wrong pw'));
				}
				
			}
		});
	}));
	
};