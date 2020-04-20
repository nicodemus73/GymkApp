const path = require('path');
const express = require('express');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const app = express();
const dotenv = require('dotenv').config('.env');

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
app.set('port', process.env.PORT || 3001);

// Stating the server
app.listen(app.get('port'), () => {
    console.log(`Server on port ${app.get('port')}`);
});
