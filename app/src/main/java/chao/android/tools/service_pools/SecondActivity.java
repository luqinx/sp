package chao.android.tools.service_pools;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import chao.android.tools.service_pools.path.PathService;
import chao.java.tools.servicepool.annotation.Service;


/**
 * @author luqin
 * @since 2019-08-26
 */
public class SecondActivity extends AppCompatActivity {

//    @Service
//    private CommonPrinter commonPrinter;

    @Service(path = "/app/path")
    private PathService pathService;



    SecondPrinter secondPrinter = new SecondPrinter();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        secondPrinter.print();

        pathService.print();
    }

    public class SecondPrinter implements Printer {

        @Override
        public void print() {
            System.out.println("I'm in second activity.");
//            commonPrinter.print();
        }
    }
}
