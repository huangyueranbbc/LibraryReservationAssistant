package university.huangyueran.polytechnic.com.libraryreservationassistant.http.protocol;

import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.EUDataGridResult;

/**
 * 主题列表网络数据解析
 * Created by huangyueran on 2017/1/17.
 */
public class TopicListProtocol extends BaseProtocol<EUDataGridResult> {

    @Override
    public String getKey() {
        return "app";
    }

    @Override
    public String getParams() {
        return "";
    }

    @Override
    public EUDataGridResult parseData(String result) {
        try {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
