package me.juliasson.unipath;

//public class ParseLogInterceptor implements ParseNetworkInterceptor{
//    @Override
//    public ParseHttpResponse intercept(Interceptor.Chain chain) throws IOException {
//        ParseHttpRequest request = chain.getRequest();
//
//        ParseHttpResponse response = chain.proceed(request);
//
//        // Consume the response body
//        ByteArrayOutputStream responseBodyByteStream = new ByteArrayOutputStream();
//        int n;
//        byte[] buffer = new byte[1024];
//        while ((n = response.getContent().read(buffer, 0, buffer.length)) != -1) {
//            responseBodyByteStream.write(buffer, 0, n);
//        }
//        final byte[] responseBodyBytes = responseBodyByteStream.toByteArray();
//        Log.i("Response_Body", new String(responseBodyBytes));
//
//        // Make a new response before return the response
//        response = new ParseHttpResponse.Builder(response)
//                .setContent(new ByteArrayInputStream(responseBodyBytes))
//                .build();
//
//        return response;
//    }
//}
