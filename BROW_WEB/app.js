var express = require('express');
var app = express();
var loginController = require('./controller/loginController');
app.set('view engine','ejs');
app.use(express.static('./public'));
loginController(app);

