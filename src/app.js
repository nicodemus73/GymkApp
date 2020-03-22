const path = require('path');
const express = require('express');
const mongoose = require('mongoose');
const app = express();

// connecting to db
mongoose.connect('mongodb://localhost/crud-mongo')
    .then(db => console.log('Db connected'))//promesa
    .catch(err => console.log(err));

//importando rutas
const indexRoutes = require('./server/routes/index');

//settings
//app.set('port', process.env.PORT || 3000);

app.use(express.urlencoded({extended: false})) //entender json, el false es porque
//solo sera texto y no ocupa mucho --> cambiar

//routes
app.use('/', indexRoutes);

// stating the server
//app.listen(app.get('port'), () => {
//console.log(`Server on port ${app.get('port')}`);
//});
app.listen(3001, () => {
    console.log('server port 3001');
});
