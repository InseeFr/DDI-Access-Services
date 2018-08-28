FROM node:8
# App directory
WORKDIR /usr/src/DDIAccessServicesTests
COPY package*.json ./

RUN npm install
COPY . .
EXPOSE 3000
CMD [ "npm", "start" ]
