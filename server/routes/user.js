const express = require('express');
const router = express.Router();

const Task = require('../bbdd/Task'); //per poder utilitzar la bbdd

router.get('/', (req, res) => {
    res.send("This is the user route");
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
        console.log(req.body.title);
        res.json(savedUser);
    } catch(err) {
        res.json({ message: err});
    }
});

router.post('/add2', async(req, res) => { 

    try {
       // console.log(req.body);
        res.json(req.body);
    } catch(err) {
        res.json({ "message": err});
    }
});

router.post('/add3', async(req, res) => { 

    try {
        const task = new Task({
            username: req.body.username,
            password: req.body.password,
        });
        const savedUser = await task.save();
        res.json(savedUser);
    } catch(err) {
        res.json({ "message": err.message});
    }
});

module.exports = router;