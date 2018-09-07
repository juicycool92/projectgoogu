
module.exports = function(app, passport,sessionManager,fs) {
	var logModule = require('../../modules/logModules.js');

	app.get('/login', function(request, response,next) {
		logModule.test('hello');
		sessionManager.sessionCheck(request,function(login,username){
			if(login){
				response.render('index',{'login':login,'userId':username});
			}else{
				response.render('./login/loginPage',{'login':login,'userId':username,'message':request.flash('loginMessage')});
			}
		});
	});
	
	app.post('/login',passport.authenticate('login',{
		successRedirect : '/',
		failureRedirect : '/login',
		failureFlash : true
	}));//for web, 결과값은 ejs를 리턴한다.
	
	app.post('/loginForAndroid',function(req,res,next){
		passport.authenticate('login', {session: false}, function(err, user, info) {
			if (err) {
				console.log('auth err'+err);
				res.status(203);
				res.send();
				return;
			}
			if (!user) {
				console.log('no user :body'+req.body.userId);
				res.status(203);
				res.send();
				return;
			}
			req.logIn(user, function(err) {
			  if (err) {
				console.log('login err'+err);
				res.status(203);
				res.send();
				return;
			  }
			  console.log('login success ');
			  res.status(202);
			  res.send();
			  return;
			});
		  })(req, res, next);
	});
	
	// app.post('/loginForAndroid',passport.authenticate('login'),function(req,res){
		
	// 	console.log(req.headers);
	// 	console.log('header send? :'+res.headersSent);
	// 	res.status(202);
	// 	//res.user=req.user;
	// 	res.send();
	// 	console.log('android login success'); 
	// 	console.log('header send? :'+res.headersSent);
		
		
		
	// });
	app.post('/sessionCheckForAndroid',(req,res)=>{
		sessionManager.sessionCheck(req,function(login,username){
			console.log('session check userId:'+login+' or username :'+username);
			console.log('req.session is '+req.headers);
			for(var i in req.headers){
				console.log(i+':'+req.headers[i]+'\n');
			}
			if(login){
				res.status(202);
			}else{
				res.status(203);
			}
			res.send();
		});
		// console.log('android userToken['+req.userToken+'] auth userId['+req.user+']');
		// if(!req.user){
		// 	res.status(203);
		// }else{
		// 	res.status(202);
		// }
		// res.send();
	});
	
	app.get('/logout', function(request, response) {
		sessionManager.sessionCheck(request,function(login,username){
			if(login){
				fs.appendFile('test.txt', '[INFO][routes/r_login][logoutResult]['+login+':'+username+'][SUCCESS]\n', ()=> {});
				request.logout();
				response.render('index',{'login':false,'userId':'guest'});
			}else{
				fs.appendFile('test.txt', '[INFO][routes/r_login][logoutResult]['+login+':'+username+'][FALSE]\n', ()=> {});
				response.render('index',{'login':login,'userId':username});
			}
		});
	});
	
};
