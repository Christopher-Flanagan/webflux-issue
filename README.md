# webflux-issue

This is a very basic small copy of some code from our application.

The HttpOutboundGatewayFlow is the flow that we used to make the http requests.

Our normal flow is to consume messages from a message broker such as Azure asb and push this info to our internal apis.

The following are the list of important line numbers in the log:

```Line 1 - passed message into HTTP flow - (Message 1)```

```Line 2 - log stating that there is request being made to a api - test-one-api (REQUEST 1)```

```Line 64 - response status code for Request 1```

```Line 118 - [response_completed] ```

```Line 675 - Message gateway timeout triggered 30 seconds after Message 1 was passed into the message gateway```

```Line 776-779 - Timeout handler completed and function related to Message 1 returned the response from the timeout handler```

```Line 781 - Outbound gateway handler releases response from REQUEST 1 ```

```Line 868 - Continuing the flow with the data returned from REQUEST 1 and sending data again to test-one-api (REQUEST 2) ```

```Line 1107 - passed a new message into HTTP flow - (Message 2)```

```Line 1144 - Sending request to the test-two-api - (REQUEST 3) ```

```Line 1170 - Response status code for REQUEST 2```

```Line 1233 - Response payload is released from the handler for REQUEST 2 - No timeout occured```

```Line 1261 - Flow attempts to return 'transformed' Message 1 but this gateway has already timed out.```

```Line 1270 - Response status code for REQUEST 3```

```Line 1327 - Error handling for INTERAL_SERVER_ERROR from REQUEST 3```

```Line 1366 - Function related to Message 2 completed and returned payload ```

We are unable to determine why REQUEST 1 timed out but REQUEST 2 which uses the same flow as request 1 had no issues.
