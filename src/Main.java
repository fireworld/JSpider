import java.net.MalformedURLException;
import java.net.URI;

public class Main {

    public static void main(String[] args) throws MalformedURLException {
        String url = "http://www.baidu.com:80/test/tomato/search.html";
        URI uri = URI.create(url);
        String path = "../hello/world/test.png";
        URI newUri = uri.resolve(path);
        System.out.println("original uri = " + uri.toString());
        System.out.println("new path = " + path);
        System.out.println("new uri = " + newUri);

    }
}
