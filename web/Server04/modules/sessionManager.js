
module.exports.sessionCheck = function(req,callback){
	
	if(req.isAuthenticated()){
		callback(true,req.user);
	}else{
		callback(false,'guest');
	}
	
};
