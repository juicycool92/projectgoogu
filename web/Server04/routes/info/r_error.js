module.exports = function(app){
	app.use(function(request,response){
		response.statusCode = 404;
		response.end('404 ERROR <br> no page found!');
	});
	
};