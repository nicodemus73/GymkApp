const express = require('express');
const router = express.Router();
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');


const Usuario = require('../bbdd/Usuario');
const {registerValidation, loginValidation} = require('../bbdd/Validation');

router.get('/', async(req, res) => {
    try {
        await Usuario.find({}, function (err, maps) {
            if (err) return console.error(err);
            res.json(maps);
        })
    } catch (error) {
        
    }
});


router.post('/register', async(req, res) => {

    try {
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
            //firstname: req.body.firstname,
            //lastname: req.body.lastname,
        });
            const savedUser = await usern.save(10);
            res.send({userid: usern._id});
            //res.send(savedUser);
            //console.log(req.body.username);
        // res.json(savedUser);
    } catch(err) {
        console.error(err);
        res.status(400).send(err);
    }
});


router.post('/login', async(req, res) => {
try {

    //validacion antes del login
    const {error} = loginValidation(req.body);
    if (error) return res.status(400).send(error.details[0].message);

    //el usuario ha de existir
    const username = await Usuario.findOne({username: req.body.username});
    if (!username) return res.json({error: "Username doesn't exists"});

    //validación de la contraseña

    const validPasswd = await bcrypt.compare(req.body.password, username.password);
    if(!validPasswd) return res.json({error: 'Invalid Password'});

    //crear y asignar token al usuario
    const token = jwt.sign({_id: username._id}, "dfsdkhnsdmvnkdjvn"/* per a que no hi hagin fallo de seguretat aixo hauria d'estar en un .env i intal·lar el paquet, expiresIn: */);
    res.header('Authorization', token).send(token); //s'ha de passar en format json
    //res.send('Logged in!');
} catch(err) {
    console.error(err);
    res.json({ "error": err.message });
}

});


//delete by _id (usuaris de moment) No s'hauria de borrar, donar de baixa

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