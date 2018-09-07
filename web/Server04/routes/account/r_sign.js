module.exports = function(app,fs,pool,passport,sessionManager,multer,storage,upload){


	app.get('/sign',function(request,response){
		response.render('./sign/signPage.ejs',{'message':request.flash('loginMessage')});
	});
	
	app.post('/sign',passport.authenticate('sign',{
		successRedirect : '/',
		failureRedirect : '/sign',
		failureFlash : true
	}));
	app.post('/signAndroid',passport.authenticate('sign'),function(req,res){
		res.json({"result":"true"});
		res.send();
	});
	app.post('/signinForAndroid',upload.single('image'),function(req,res,next){
		passport.authenticate('signForAndroid', {session: false}, function(err, user, info) {
			if (err) {
				console.log('signin err'+err);
				res.status(401);
				res.send();
				return;
			}else if (!user) {
				console.log('signin no user :'+req.body.userId);
				res.status(401);
				res.send();
				return;
			}else{
				res.status(201);
				res.send();
			}
		  })(req, res, next);
	});
	
	app.get('/loadAddressPopupForMobile',(req,res)=>
	{
		res.render('./sign/addressWrite.ejs');
	});
};
