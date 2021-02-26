import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Utils {

    public enum Status {
        KhoiTao(1),
        DangTienHanh(2),
        DaThucHien(3),
        XemXet(7),
        HoanThanh(8),
        PhanHoi(4),
        Dong(5),
        TuChoi(6),
        QuaHan(9);

        private int idStatus;

        private Status(int idStatus) {
            this.idStatus = idStatus;
        }

        public static int getIdStatus(Status status) {
            return status.idStatus;
        }

    }

    public static String addDays(Date date, Integer days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return new SimpleDateFormat("dd/MM/yyyy ").format(cal.getTime());
    }

    static <T> List<List<T>> subList(List<T> list, final int L) {
        List<List<T>> parts = new ArrayList<List<T>>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<T>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
    }

}
