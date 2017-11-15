import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cxx on ${DATA}.
 * xx.ch@outlook.com
 */
public interface BrowserVersion {
   BrowserVersion CHROME = new BrowserVersion() {
       private final Map<String, List<String>> headers = new HashMap<>();

      {
         headers.put("Accept", Collections.singletonList("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"));
         headers.put("Accept-Encoding", Collections.singletonList("gzip, deflate"));
         headers.put("Accept-Language", Collections.singletonList("zh-CN,zh;q=0.9,en;q=0.8"));
         headers.put("Cache-Control", Collections.singletonList("max-age=0"));
         headers.put("Connection", Collections.singletonList("keep-alive"));
         headers.put("User-Agent", Collections.singletonList("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36"));

      }

      @Override
      public Map<String, List<String>> headers() {
         return headers;
      }
   };

   default Map<String, List<String>> headers() {
      return Collections.emptyMap();
   }
}
