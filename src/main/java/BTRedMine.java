import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Issue;
import model.Project;
import model.RootIssue;
import model.RootProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class BTRedMine {
    private static BTRedMine instance;
    int offset = 0;
    String urlIssue = "http://10.192.1.23/redmine/issues.json?key=e535c06d7b1dd1cf1c7558da605d631ecd09e15b&limit=100&offset=%d&project_id=%d";
    String urlPrj = "http://10.192.1.23/redmine/projects.json?key=e535c06d7b1dd1cf1c7558da605d631ecd09e15b&limit=100&offset=0";

    public static BTRedMine getInstance() {
        if (instance == null)
            instance = new BTRedMine();
        return instance;
    }

    /**
     * Lay danh sach cac project tren he thong
     *
     * @return danh sach project
     */
    public List<Project> getAllProjects() {
        try {
            URL obj = new URL(urlPrj);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                // print result
                RootProject rootProject = new Gson().fromJson(response.toString(), RootProject.class);
                return rootProject.projects;//.stream().filter(project -> project.parent == null).collect(Collectors.toList());
            } else {
                return new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private RootIssue _getIssues(int offset, int idProject) {
        try {
            URL obj = new URL(String.format(urlIssue, offset, idProject));
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                // print result
                Gson g = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
                return g.fromJson(response.toString(), RootIssue.class);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Issue> getAllIssuesInProject(int idProject) {
        RootIssue _rootIssue = _getIssues(offset, idProject);
        if (_rootIssue != null) {
            //check xem con issue k
            if (_rootIssue.total_count > offset) {
                List<Issue> lst = new ArrayList<>(_rootIssue.issues);
                while (true) {
                    offset += 100;
                    RootIssue r = _getIssues(offset, idProject);
                    assert r != null;
                    if (r.issues.size() == 0) {
                        break;
                    }
                    lst.addAll(r.issues);
                }
                return lst;
            } else
                return _rootIssue.issues;
        }
        return new ArrayList<>();
    }

    public int statusIssue(List<Issue> lst, Utils.Status status) {
        lst = lst.stream().filter(issue -> issue.status.id == Utils.Status.getIdStatus(status)).collect(Collectors.toList());
        return lst.size();
    }

    public String Report() {
        StringBuilder sb = new StringBuilder();
        sb.append("Báo cáo tiến độ công việc ngày: ");
        sb.append(Utils.addDays(new Date(), -1));
        sb.append("\n");
        for (Project p : getAllProjects()) {
            sb.append("Dự án: ");
            sb.append(p.name);
            sb.append("\n");
            List<Issue> lstIssue = getAllIssuesInProject(p.id);
            sb.append("Công việc đang tiến hành: ");
            sb.append(statusIssue(lstIssue, Utils.Status.DangTienHanh));
            sb.append("\n");
            sb.append("Công việc đã thực hiện: ");
            sb.append(statusIssue(lstIssue, Utils.Status.DaThucHien));
            sb.append("\n");
            sb.append("Công việc đã hoàn thành: ");
            sb.append(statusIssue(lstIssue, Utils.Status.HoanThanh));
            sb.append("\n");
            sb.append("Công việc quá hạn: ");
            sb.append(statusIssue(lstIssue, Utils.Status.QuaHan));
            sb.append("\n");
            sb.append("----------------");
            sb.append("\n");
            offset = 0;
        }
        return sb.toString();
    }
}
