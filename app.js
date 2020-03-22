const express = require('express');
const app = express();

// Middlewares. They execute when accessing the route.
/*
app.use('/llocs', () => {
    console.log('This is a middleware running');
    // Es pot usar per autentificar
});
*/

//Import routes
const postsRoute = require('./routes/posts');
const usersRoute = require('./routes/users');

app.use('/posts', postsRoute);
app.use('/users', usersRoute);

// Start listening
app.listen(3000);


