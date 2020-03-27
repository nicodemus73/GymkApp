const path = require('path');
const express = require('express');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const app = express();

// DB connection
mongoose.set('useUnifiedTopology', true);
mongoose.set('useCreateIndex', true);
mongoose.connect('mongodb://localhost/crud-mongo',  { useNewUrlParser: true }) //nom de la bbdd
    .then(db => console.log('DB connected!'))//promesa
    .catch(err => console.log(err));

// Middlewares
app.use(express.urlencoded({ extended: false }));
app.use(express.json());

// Routes
app.use('/', require('./routes/home'));
app.use('/user', require('./routes/user'));
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
