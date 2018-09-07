var express=require('express');
var app = express();

var fs = require('fs');
//var privateKey  = fs.readFileSync('certificates/key.pem', 'utf8');
//var certificate = fs.readFileSync('certificates/cert.pem', 'utf8');
//var credentials = {key: privateKey, cert: certificate};
//this is for https. enable it when its ready.

var admin = require("firebase-admin");

var serviceAccount = require('./config/serviceAccountKey.json');

admin.initializeApp({
	  credential: admin.credential.cert(serviceAccount),
	  databaseURL: ""
	});

var registrationToken = '';
var payload = {    
		notification: {
		    title: "테스트",
		    body: "제발좀떠줘제발좀떠줘제발좀떠줘제발좀떠줘제발좀떠줘제발좀떠줘제발좀떠줘제발좀떠줘제발좀떠줘제발좀떠줘제발좀떠줘제발좀떠줘제발좀떠줘제발좀떠줘제발좀떠줘제발좀떠줘제발좀떠줘제발좀떠줘제발좀떠줘제발좀떠줘"
		  },data:{  
			  test:"hello하하히히히호호후훟헤헤하하하하아하하 "
		  }    
		};

		// Send a message to the device corresponding to the provided
		// registration token.
		admin.messaging().sendToDevice(registrationToken, payload)
		  .then(function(response) {
		    // See the MessagingDevicesResponse reference documentation for
		    // the contents of response.
		    console.log("Successfully sent message:", response);
		  })
		  .catch(function(error) {
		    console.log("Error sending message:", error);
		  });
//above code is settings for google firebase, push server. pause due to developing priority/


var bodyParser = require('body-parser');
var SQLinfo = require('./config/db/db_info');
var mainModule = require('./modules/mainPageModules.js');
var tradeModule = require('./modules/tradeModules.js');
var driverModule = require('./modules/driverModules.js');
var chatModule = require('./modules/chatModules.js');
var locationModule = require('./modules/locationModules.js');
//modules

var jsonWriter = require('./config/jsonCreater.js');
var format = require('date-format');
//utility

var flash = require('connect-flash');
var passport = require('passport'); 
var localStrategy = require('passport-local').Strategy;
var session = require('express-session');
//for authentication




var multer = require('multer');
var storage = multer.memoryStorage();
var upload = multer({storage: storage});
//for file manage.

var morgan = require('morgan');
var accessLogStream = fs.createWriteStream(__dirname + '/access.log', {flags: 'a'});
app.use(morgan('combined',  {"stream": accessLogStream}));
//for log

var sql = require('mysql');
var pool = sql.createPool(SQLinfo);
//sql manage

app.use(session({
	secret:'k'
	resave : true,
	saveUninitialized: true
}));

app.set('viewss',__dirname+'/views');
app.set('view engine','ejs');
app.use(express.static('public')); //css파일,이미지,동영상등 변하지 않는 리소스들을 보관하는 폴더엔 public 내부를 인식.
app.use('/static', express.static('public'));
const _SERVER_PORT = 8082;
var server = app.listen(_SERVER_PORT,function(){
	console.log(_SERVER_PORT+'server is up');
});
//var https = require('https');
//var httpsServer = https.createServer(credentials,app);
//httpsServer.listen(8443);
// this is for https.

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: false}));
app.use(passport.initialize());
app.use(passport.session());
app.use(flash());
//express settings.

var _passport = require('./config/passport')(passport);
//authentication module

var _sessionManager = require('./modules/sessionManager.js');
//for manage sessions

var r_chat = require('./routes/chat/r_chat.js')(app,passport,_sessionManager,tradeModule,chatModule);
var r_main = require('./routes/r_main.js')(app,fs,pool,jsonWriter,mainModule,_sessionManager);
var r_login = require('./routes/account/r_login.js')(app,passport,_sessionManager,fs);
var r_sign = require('./routes/account/r_sign.js')(app,fs,pool,passport,_sessionManager,multer,storage,upload);
var r_error = require('./routes/info/r_error.js')(app);
var r_info = require('./routes/account/r_info.js')(app,pool,passport,_sessionManager,locationModule,jsonWriter);
var r_trade = require('./routes/board/r_trade.js')(app,passport,_sessionManager,tradeModule,jsonWriter,format);
var r_driver = require('./routes/account/r_driver.js')(app,passport,jsonWriter,driverModule,_sessionManager);
//routes

//below codes are for test function//
//var results = testfunction.calculate(35.2676744,129.0785277,1);
//console.log('result :'+JSON.stringify(results));