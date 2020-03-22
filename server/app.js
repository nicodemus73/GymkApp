const path = require('path');
const express = require('express');
const mongoose = require('mongoose');
const app = express();

// connecting to db
mongoose.connect('mongodb://localhost/crud-mongo')
    .then(db => console.log('DB connected!'))//promesa
    .catch(err => console.log(err));

//Importando Rutas

//const homeRoutes = require('./routes/home');
//app.use('/', homeRoutes);

app.use('/', require('./routes/home'));
app.use('/user', require('./routes/user'));
app.use('/map', require('./routes/map'));
app.use('/game', require('./routes/game'));

//settings
//app.set('port', process.env.PORT || 3000);

// Middlewares. They execute when accessing the route.
/*
app.use('/user', () => {
    console.log('This is a middleware running');
    // Es pot usar per autentificar!
});
*/

app.use(express.urlencoded({extended: false})) //entender json, el false es porque
//solo sera texto y no ocupa mucho --> cambiar

// stating the server
//app.listen(app.get('port'), () => {
//console.log(`Server on port ${app.get('port')}`);
//});
app.listen(3001, () => {
    console.log('Server running at port: 3001');
});