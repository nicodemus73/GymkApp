const express = require('express');
const router = express.Router();
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

const Task = require('../bbdd/Task'); //per poder utilitzar la bbdd
const Usuario = require('../bbdd/Usuario');
const {registerValidation, loginValidation} = require('../bbdd/Validation');

router.get('/', (req, res) => {
    res.send("This is the user route");
});

router.get('/hola', (req, res) => {
    res.send("hello");
});

router.get('/adeu', (req, res) => {
    res.send("adeu");
});



router.post('/register', async(req, res) => {

    //validar antes de crear el usuario

    const {error} = registerValidation(req.body);
    if (error) return res.status(400).send(error.details[0].message);

    //validar que username sigui unic

    const usernameExist = await Usuario.findOne({username: req.body.username});
    if (usernameExist) return res.status(400).send("Username already exists");

    //hashear la password

    const salt = await bcrypt.genSalt();
    const hashpsswd = await bcrypt.hash(req.body.password, salt);

    //crea l'usuari

    const usern = new Usuario ({
        username: req.body.username,
        password: hashpsswd,//req.body.password,
        firstname: req.body.firstname,
        lastname: req.body.lastname,
    });
    try {
        const savedUser = await usern.save(10);
        res.send({userid: usern._id});
        //res.send(savedUser);
        //console.log(req.body.username);
       // res.json(savedUser);
    } catch(err) {
        res.status(400).send(err);
    }
});

router.post('/login', async(req, res) => {

    //validacion antes del login
    const {error} = loginValidation(req.body);
    if (error) return res.status(400).send(error.details[0].message);

    //el usuario ha de existir
    const username = await Usuario.findOne({username: req.body.username});
    if (!username) return res.status(400).send("Username doesn't exists");

    //validación de la contraseña

    const validPasswd = await bcrypt.compare(req.body.password, username.password);
    if(!validPasswd) return res.status(400).send('Invalid Password');

    //crear y asignar token al usuario
    const token = jwt.sign({_id: username._id}, "dfsdkhnsdmvnkdjvn"/* per a que no hi hagin fallo de seguretat aixo hauria d'estar en un .env i intal·lar el paquet */);
    res.header('Authorization', token).send(token);
    //res.send('Logged in!');

});


router.post('/add', async(req, res) => { //guardar es un exemple, a millorar
    const task = new Task({
        title: req.body.title,
        author: req.body.author,
        description: req.body.description
    });
    try {
        const savedUser = await task.save();
        console.log(task);
       // console.log(req.body.title);
        res.json(savedUser);
    } catch(err) {
        res.json({ message: err});
    }
});

//delete by _id (usuaris de moment)

router.post('/delete/:id', async function(req, res) {

    Usuario.findByIdAndDelete(req.params.id)
    .exec()
    .then(doc => {
        //console.log(doc);
        if (!doc) {return res.status(404).send('Document not found').end();}
        return res.send('File deleted').end();
    })
    .catch (error =>
        res.json({ message: error}));

 });

module.exports = router;