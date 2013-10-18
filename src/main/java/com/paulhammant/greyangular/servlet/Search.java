package com.paulhammant.greyangular.servlet;

import com.google.api.client.http.AbstractHttpContent;
import com.google.api.client.http.HttpResponse;
import com.paulhammant.greyangular.Constants;
import com.paulhammant.greyangular.LocationMap;
import com.paulhammant.greyangular.OutgoingDoSearchContent;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Search extends AbstractJsonpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        writeJsonpResponse(resp, req.getParameter("callback"),
                doSearch(req.getParameter("from"),
                        req.getParameter("to"),
                        req.getParameter("when"),
                        req.getSession().getAttribute(Constants.SESSION_KEY),
                        LocationMap.getOrMakeMap(req)));
    }

    public String doSearch(String from, String to, String when, Object ghSessionId, LocationMap locationMap) throws IOException {
        AbstractHttpContent content = new OutgoingDoSearchContent(from, to, when, locationMap);
        HttpResponse resp = postRequest(url("/services/farefinder.asmx/Search"),
                content, ghSessionId).execute();
        String respContent = resp.parseAs(String.class);
        if (respContent.contains("\"RedirectUrl\":\"/farefinder/step2.aspx\"")) {
            respContent = getRequest(url("/farefinder/step2.aspx"), ghSessionId)
                    .execute().parseAs(String.class);
            // String detail = getDetail(respContent);
            return getSchedule(respContent);
        } else {
            return respContent;
        }
    }

    private String getSchedule(String respContent) {
        String startStr = "FareFinder.Step2.Initialize(";
        int start = respContent.indexOf(startStr);
        int end = respContent.indexOf("]]},");
        return respContent.substring(start + startStr.length(), end+3);
    }

    private String getDetail(String respContent) {
        String startStr = "FareFinder.SearchControl.InitializeMicrosite('";
        int start = respContent.indexOf(startStr);
        int end = respContent.indexOf("}]')");
        return respContent.substring(start + startStr.length(), end+2);
    }

}
