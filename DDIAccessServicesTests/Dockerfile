FROM node:latest

ADD ./app /usr/src/app
WORKDIR /usr/src/app

RUN pwd 

RUN /bin/ls

RUN npm install

EXPOSE 3000

CMD ["npm", "start"]
