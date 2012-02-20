package com.barinek.gnip;

import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class TrackConsumer implements Runnable {
    private static String powerTrackUrl;
    private static String powerTrackUserAndPassword;

    static {
        powerTrackUrl = System.getenv("GNIP_POWER_TRACK_URL");
        powerTrackUserAndPassword = System.getenv("GNIP_POWER_TRACK_USER_AND_PASSWORD");
    }

    private final KeywordDAO keywordDAO;

    public static void main(String[] args) {
        TrackConsumer consumer = new TrackConsumer();
        consumer.run();
    }

    public TrackConsumer() {
        keywordDAO = new KeywordDAO();
    }

    public void run() {
        CookieHandler.setDefault(new CookieManager());

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(powerTrackUrl);

            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(1000 * 60 * 60);
            connection.setConnectTimeout(1000 * 10);
            connection.setRequestProperty("Authorization", "Basic " + new String(Base64.encodeBase64((powerTrackUserAndPassword).getBytes("UTF-8")), "UTF-8"));

            inputStream = connection.getInputStream();
            int responseCode = connection.getResponseCode();

            if (responseCode >= 200 && responseCode <= 299) {
                StreamIterator streamIterator = new StreamIterator(inputStream);

                while (streamIterator.hasNext()) {
                    String next = streamIterator.next();
                    System.out.println(next);

                    for (String knownCity : WebApplication.knownCities) {
                        if (next.indexOf(knownCity) > 0) {
                            keywordDAO.incrementCount(knownCity);
                        }
                    }
                }

            } else {
                int responseCode1 = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                System.out.println("Non-success response: " + responseCode1 + " -- " + responseMessage);
            }
        } catch (Exception e) {
            if (connection != null) {
                try {
                    int responseCode = connection.getResponseCode();
                    String responseMessage = connection.getResponseMessage();
                    System.out.println("Non-success response: " + responseCode + " -- " + responseMessage);
                } catch (IOException ignore) {
                }
            }
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    public class StreamIterator implements Iterator<String> {
        private final Reader reader;
        private String cachedLine;

        public StreamIterator(InputStream inputStream) throws UnsupportedEncodingException {
            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        }

        public boolean hasNext() {
            return cachedLine != null || fill();
        }

        public String next() {
            if (hasNext()) {
                return getAndResetCachedLine();
            }
            return null;
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove not supported for tweet iterators.");
        }

        private String getAndResetCachedLine() {
            String returnVal = cachedLine;
            cachedLine = null;
            return returnVal;
        }

        private boolean fill() {
            StringBuilder builder = new StringBuilder(4096);
            boolean startedObject = false;
            while (true) {
                try {
                    int currentChar = reader.read();

                    if (currentChar == '\n' && !startedObject) {
                    } else if (currentChar == ' ' && !startedObject) {
                    } else if (currentChar == '\r' && !startedObject) {
                    } else if (currentChar == -1) {

                        closeQuietly(reader);
                        if (builder.length() > 0) {
                            cachedLine = builder.toString();
                            return true;
                        } else {
                            return false;
                        }
                    } else if (currentChar == '\r') {
                        cachedLine = builder.toString();
                        return cachedLine.length() > 0;
                    } else if (currentChar == '{') {
                        startedObject = true;
                        builder.append((char) currentChar);
                    } else {
                        builder.append((char) currentChar);
                    }
                } catch (IOException e) {
                    closeQuietly(reader);
                    return false;
                }
            }
        }

        private void closeQuietly(Reader reader) {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ignore) {
            }
        }
    }
}
