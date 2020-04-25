FROM node:latest
WORKDIR /usr/src/app
COPY server/package.json ./
RUN npm install
COPY ./server /usr/src/app
EXPOSE 3001
CMD [ "node", "app.js" ]
