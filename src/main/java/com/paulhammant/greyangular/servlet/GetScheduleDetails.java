package com.paulhammant.greyangular.servlet;

import com.google.api.client.http.AbstractHttpContent;
import com.paulhammant.greyangular.Constants;
import com.paulhammant.greyangular.OutgoingGetScheduleContent;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GetScheduleDetails extends AbstractJsonpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String key = req.getParameter("key").replace("__HASH__", "#");
        String result = getSchedules(key, req.getSession().getAttribute(Constants.SESSION_KEY));
        writeJsonpResponse(resp, req.getParameter("callback"), result);
    }

    public String getSchedules(String key, Object ghSessionId) throws IOException {

        AbstractHttpContent content = new OutgoingGetScheduleContent(key);
        String respContent = postRequest(url("/services/farefinder.asmx/GetScheduleDetails"),
                content, ghSessionId).execute().parseAs(String.class);
        String startStr = "\"Items\":";
        int start = respContent.indexOf(startStr);
        int end = respContent.indexOf("}]}}");
        return respContent.substring(start + startStr.length(), end + 2);

    }

}
