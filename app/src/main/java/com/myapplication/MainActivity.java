package com.myapplication;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> observableEmitter) throws Exception {
                Bitmap bitmap = Picasso
                        .get()
                        .load("http://diz36nn4q02zr.cloudfront.net/webapi/imagesV3/Original/SalePage/3867040/0/163324?v=1").get();
                observableEmitter.onNext(bitmap);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Observer<Bitmap>() {
            @Override
            public void onSubscribe(Disposable disposable) {

            }

            @Override
            public void onNext(Bitmap bitmap) {
                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
                Log.d("Ted","onnext");
                FirebaseVisionLabelDetector detector = FirebaseVision.getInstance()
                        .getVisionLabelDetector();
                Task<List<FirebaseVisionLabel>> result =
                        detector.detectInImage(image)
                                .addOnSuccessListener(
                                        new OnSuccessListener<List<FirebaseVisionLabel>>() {
                                            @Override
                                            public void onSuccess(List<FirebaseVisionLabel> labels) {
                                                // Task completed successfully
                                                // ...
                                                for (FirebaseVisionLabel label: labels) {
                                                    String text = label.getLabel();
                                                    String entityId = label.getEntityId();
                                                    float confidence = label.getConfidence();
                                                    String s = String.format("%s %s %s", text, entityId, confidence);
                                                    Log.d("Ted","result " + s);
                                                }

                                            }
                                        })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Task failed with an exception
                                                // ...
                                            }
                                        });
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });


    }
}
