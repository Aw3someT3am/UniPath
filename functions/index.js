let functions = require('firebase-functions');

let admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('/users/{userId}').onWrite((snapshot, context) => {

	// ///get the userId of the person receiving the notification because we need to get their token
	const receiverId = context.params.userId;
	console.log("receiverId: ", receiverId);
  // const receiverId = event.data.child('user_id').val();
	// console.log("receiverId: ", receiverId);

	// ///get the message
	// // const message = event.data.child('message').val();
	// // console.log("message: ", message);
  // const date = snapshot.parent.child('dates').child('custom_deadline').val();
  // console.log("dare: ", date);

  // const username = snapshot.ref.parent.child('username').val();
  // console.log("username: ", username);

	// ///get the message id. We'll be sending this in the payload
	// const messageId = event.params.messageId;
	// console.log("messageId: ", messageId);
  //
	// ///query the users node and get the name of the user who sent the message
	// return admin.database().ref("/users/" + senderId).once('value').then(snap => {
	// 	const senderName = snap.child("name").val();
	// 	console.log("senderName: ", senderName);
  //
	// 	///get the token of the user receiving the message
		return admin.database().ref("/users/" + receiverId).once('value').then(snap => {
      const username = snap.child("username").val();
			//console.log("token: ", token);
      console.log("user: ", username);

      return admin.database().ref("/users/" + receiverId).once('value').then(snap1 => {
        const token = snap1.child("messaging_token").val();
      // This registration token comes from the client FCM SDKs.
      //const registrationToken = 'c_DV0-f7imo:APA91bGGLfzuFXtEZWP35qd1Orw73EBE1D61r2JnUhGaCe4r0_YXXZvFueS30y8iO1wUXMhY3uH0GhanjX1gT86E_MGDVLXQLm1tWWpgiTr1fYpEgt8C1_sdtVPIQ918xJW9CHLdy-5Mu3veAyrNqFYIFSTWNCRA1Q';

        // See documentation on defining a message payload.
        const payload = {
          data: {
            data_type: 'reminder',
            title: "Hey " + username + "!",
            message: "Deadline coming up",
          },
          notification: {
            title: "Hey " + username + "! you have a deadline comming up",
            body: "Deadline coming up"
          },
        };

        // Send a message to the device corresponding to the provided
        // registration token.
        //admin.messaging().send(message)
          // .then((response) => {
          //   // Response is a message ID string.
          //   console.log('Successfully sent message:', response);
          // })
          // .catch((error) => {
          //   console.log('Error sending message:', error);
          // });
          return admin.messaging().sendToDevice(token, payload);
        });

      });

    });

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
