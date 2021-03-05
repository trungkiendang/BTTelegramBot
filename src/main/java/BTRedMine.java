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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class BTRedMine {
    private static BTRedMine instance;
    int offset = 0;
    String urlIssue = "http://14.160.26.174:1080/redmine/issues.json?key=e535c06d7b1dd1cf1c7558da605d631ecd09e15b&limit=100&offset=%d&project_id=%d";
    String urlPrj = "http://14.160.26.174:1080/redmine/projects.json?key=e535c06d7b1dd1cf1c7558da605d631ecd09e15b&limit=100&offset=0";

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
                offset = 0;
                return lst;
            } else
                offset = 0;
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
        }
        return sb.toString();
    }

    public void RDP(String chatId) {
        List<Issue> lstIssue = new ArrayList<>();
        //lay het cac project
        List<Project> lstProject = getAllProjects();
        for (Project p : lstProject) {
            List<Issue> _l = getAllIssuesInProject(p.id);
            ErrorLogger.getInstance().log("Project size: " + _l.size());
            lstIssue.addAll(_l);
        }
        lstIssue = lstIssue.stream().filter(Utils.distinctByKey(p -> p.id)).collect(Collectors.toList());

        lstIssue = lstIssue.stream().filter(m -> m.assigned_to != null).collect(Collectors.toList());
        Map<String, List<Issue>> lstGroupded = lstIssue.stream().collect(Collectors.groupingBy(w -> w.assigned_to.name));
        Set<String> groupedNameKeySet = lstGroupded.keySet();
        List<String> groupedNameKeySetSorted = new ArrayList<>(groupedNameKeySet).stream().sorted().collect(Collectors.toList());
        Date date = new Date();
        SimpleDateFormat xxx = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat yyy = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder sb = new StringBuilder();
        sb.append("Báo cáo ngày: ");
        sb.append(yyy.format(date));
        sb.append("\n");
        new BotJob().SendMessage(sb.toString(), chatId);
        sb = new StringBuilder();
        for (String s : groupedNameKeySetSorted) {
            List<Issue> _ls = lstGroupded.get(s).stream().filter(m -> m.start_date.equals(formatter.format(date))).collect(Collectors.toList());
            if (_ls.size() != 0) {
                sb.append("<b>");
                sb.append(s);
                sb.append(":");
                sb.append("</b>");
                sb.append("\n");
                for (Issue i : _ls) {
                    sb.append(i.project.name);
                    sb.append(": ");
                    sb.append(i.subject);
                    sb.append(" - ");
                    sb.append(i.status.name);
                    sb.append("\n");
                    if (i.description.length() > 0) {
                        sb.append("Mô tả công việc:\n");
                        sb.append("<i>");
                        sb.append(i.description);
                        sb.append("</i>\n");
                    }

//                    if (i.status.id == Utils.Status.getIdStatus(Utils.Status.DaThucHien) ||
//                            i.status.id == Utils.Status.getIdStatus(Utils.Status.HoanThanh)) {
//                        sb.append("Thời gian thực hiện: ");
//                        sb.append(i.)
//                    }
                }
                sb.append("-------------------------\n");
                new BotJob().SendMessage(sb.toString(), chatId);
                sb = new StringBuilder();
            }
        }
        sb = new StringBuilder();
        sb.append("<b>Thời gian tổng hợp công việc:</b> ");
        sb.append("<i>");
        sb.append(xxx.format(new Date()));
        sb.append("</i>");
        new BotJob().SendMessage(sb.toString(), chatId);
    }
}
