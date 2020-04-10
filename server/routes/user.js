const express = require('express');
const router = express.Router();
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

const User = require('../bbdd/UserSchema');
const { registerValidation, loginValidation } = require('../bbdd/Validation');

router.get('/', async (req, res) => {
    try {
        await User.find({}, function (err, users) {
            if (err) return res.status(500).json({ "error": err.message });//Internal Server Error
            res.status(200).json(users);
        })
    } catch (err) {
        res.status(500).json({ "error": err.message });//Internal Server Error
    }
});


router.post('/register', async (req, res) => {

    try {
        //validar antes de crear el usuario
        const { err } = registerValidation(req.body);
        if (err) return res.status(400).json({ "error": err.details[0].message });

        //validar que username sigui unic
        const usernameExist = await User.findOne({ username: req.body.username });
        if (usernameExist) return res.status(409).json({ "error": "Username already exists" }); //conflict

        //hashear la password 
        const salt = await bcrypt.genSalt();
        const hashpsswd = await bcrypt.hash(req.body.password, salt);

        //crea l'usuari
        const usern = new Usuario({
            username: req.body.username,
            password: hashpsswd,//req.body.password,
            //firstname: req.body.firstname,
            //lastname: req.body.lastname,
        });
        const savedUser = await usern.save(10);
        res.status(200).end();//.json({userid: usern._id}); //ok
    } catch (err) {
        console.error(err);
        res.status(400).json({ "error": err.message }); //bad request
    }
});


router.post('/login', async (req, res) => {
    try {

        //validacion antes del login
        const { err } = loginValidation(req.body);
        if (err) return res.status(400).json({ "error": err.details[0].message }); //Bad request

        //el usuario ha de existir
        const username = await User.findOne({ username: req.body.username });
        if (!username) return res.status(404).json({ "error": "Incorrect Username or Password1" }); //Not Found  no existe el usuario

        const validPasswd = await bcrypt.compare(req.body.password, username.password);
        if (!validPasswd) return res.status(404).json({ "error": "Incorrect Username or Password" }); //not found

        //crear y asignar token al usuario
        const token = jwt.sign({ _id: username._id }, process.env.TOKEN_KEY);
        res.status(200).header('Authorization', token).end();//.json({"token": token});
        //res.send('Logged in!');
    } catch (err) {
        res.status(400).json({ "error": err.message });
    }

});

//delete by _id (usuaris de moment) No s'hauria de borrar, donar de baixa
router.post('/delete/:id', async function (req, res) {

    User.findByIdAndDelete(req.params.id)
        .exec()
        .then(doc => {
            //console.log(doc);
            if (!doc) { return res.status(404).json({ "error": "Document not found" }).end(); }
            return res.status(200).json({ "message": "File deleted" }).end();
        })
        .catch(error =>
            res.status(400).json({ message: error }));

});

module.exports = router;
