const express = require('express');
const mongoose = require('mongoose');
const fs = require('fs');
const http = require('http');
const https = require('https');
const path = require('path');
const bodyParser = require('body-parser');
const dotenv = require('dotenv').config('.env');
const app = express();

// DB connection
mongoose.set('useUnifiedTopology', true);
mongoose.set('useCreateIndex', true);

// Try to find DB in localhost, if not look for docker.
mongoose.connect('mongodb://localhost/crud-mongo', { useNewUrlParser: true })
    .then(db => console.log('Localhost DB connected.'))
    .catch(err => console.log('Localhost DB not connected.'));
mongoose.connect('mongodb://172.16.16.2/crud-mongo', { useNewUrlParser: true })
    .then(db => console.log('Docker DB connected.'))
    .catch(err => console.log('Docker DB not connected.'));

// Middlewares I
app.use(express.urlencoded({ extended: false }));
app.use(express.json());

// Routes I
app.use('/user', require('./routes/user'));

// Middlewares II
app.use(require('./middlewares/verifyToken'));

// Routes II
app.use('/', require('./routes/home'));
app.use('/point', require('./routes/point'));
app.use('/map', require('./routes/map'));
app.use('/game', require('./routes/game'));
app.use('/prova', require('./routes/prova')); //de prova -> a borrar

// Settings
app.set('httpPort', process.env.HTTPPORT || 3001);
app.set('httpsPort', process.env.HTTPSPORT || 3002);

// HTTPS
var privateKey  = fs.readFileSync('certificates/serverkey.pem');
var certificate = fs.readFileSync('certificates/servercert.crt');
var credentials = {key: privateKey, cert: certificate};

// Stating the server
const httpServer = http.createServer(app);
httpServer.listen(app.get('httpPort'), () => {
    console.log(`HTTP server on port ${app.get('httpPort')}`);
});
const httpsServer = https.createServer(credentials, app);
httpsServer.listen(app.get('httpsPort'), () => {
    console.log(`HTTPS server on port ${app.get('httpsPort')}`);
});
