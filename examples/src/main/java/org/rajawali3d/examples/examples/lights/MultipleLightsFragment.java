package org.rajawali3d.examples.examples.lights;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.Animation;
import org.rajawali3d.animation.Animation3D;
import org.rajawali3d.animation.TranslateAnimation3D;
import org.rajawali3d.examples.R;
import org.rajawali3d.examples.examples.AExampleFragment;
import org.rajawali3d.lights.PointLight;
import org.rajawali3d.loader.LoaderAWD;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Sphere;

public class MultipleLightsFragment extends AExampleFragment implements
		SeekBar.OnSeekBarChangeListener {

	private SeekBar mSeekBarRotation, mSeekBarLight;
	Object3D suzanne;
	private PointLight light1;
	private Point currentObjectPoint, previousObjectPoint;
	private MotionEvent.PointerCoords curPointer1, curPointer2, prevPointer1, prevPointer2;

	@Override
    public AExampleRenderer createRenderer() {
		return new MultipleLightsRenderer(getActivity(), this);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		currentObjectPoint = new Point(0, 0);
		previousObjectPoint = new Point(0, 0);
		curPointer1 = new MotionEvent.PointerCoords();
		prevPointer1 = new MotionEvent.PointerCoords();
		curPointer2 = new MotionEvent.PointerCoords();
		prevPointer2 = new MotionEvent.PointerCoords();
		final FloatingPoint rotation = new FloatingPoint();
		mLayout.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int action = MotionEventCompat.getActionMasked(event);
				switch(action) {
					case (MotionEvent.ACTION_DOWN):
						if (event.getPointerCount() == 2) {
							System.out.println("TWO FINGER DOWN");
							rotation.twoFingers=true;
							event.getPointerCoords(0, curPointer1);
							event.getPointerCoords(0, prevPointer1);
							event.getPointerCoords(1, curPointer2);
							event.getPointerCoords(1, prevPointer2);
						} else {
							System.out.println("Action was DOWN");
							currentObjectPoint.set((int) event.getX(), (int) event.getY());
							previousObjectPoint.set(currentObjectPoint.x, currentObjectPoint.y);
						}
						return true;
					case (MotionEvent.ACTION_MOVE):
						if(event.getPointerCount() == 2) {
							System.out.println("TWO TOUCH MOVE");
							if (rotation.twoFingers==false){
								event.getPointerCoords(0, curPointer1);
								event.getPointerCoords(0, prevPointer1);
								event.getPointerCoords(1, curPointer2);
								event.getPointerCoords(1, prevPointer2);
							} else {
								prevPointer1.copyFrom(curPointer1);
								prevPointer2.copyFrom(curPointer2);
								event.getPointerCoords(0, curPointer1);
								event.getPointerCoords(1, curPointer2);
							}
							rotation.twoFingers = true;
							double v1x = (double)(curPointer1.x-prevPointer1.x);
							double v1y = (double)(curPointer1.y-prevPointer1.y);
							double v2x = (double)(curPointer2.x-prevPointer2.x);
							double v2y = (double)(curPointer2.y-prevPointer2.y);
							System.out.println((v1x+v2x)/(-7));
							rotation.x = (rotation.x + (v1x+v2x)/(-7))%360;
							suzanne.setRotY(rotation.x);
						}
						else if(!rotation.twoFingers){
							System.out.println("Action was MOVE");
							previousObjectPoint.set(currentObjectPoint.x, currentObjectPoint.y);
							currentObjectPoint.set((int) event.getX(), (int) event.getY());
							System.out.println("X:" + currentObjectPoint.x + "   Y:" + currentObjectPoint.y);
							int deltaX = currentObjectPoint.x - previousObjectPoint.x;
							int deltaY = currentObjectPoint.y - previousObjectPoint.y;
							suzanne.setX(suzanne.getX() + (deltaX / 300F));
							suzanne.setZ(suzanne.getZ() + (deltaY / 300F));
						}
						return true;
					case (MotionEvent.ACTION_UP) :
						System.out.println("Action was UP");
						rotation.twoFingers = false;
						return true;
					case (MotionEvent.ACTION_CANCEL) :
						System.out.println("Action was CANCEL");
						return true;
					case (MotionEvent.ACTION_OUTSIDE) :
						System.out.println("Movement occurred outside bounds " +
								"of current screen element");
						return true;
					default :
						//SHIT
				}
				return true;
			}
		});

		LinearLayout ll = new LinearLayout(getActivity());
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.setGravity(Gravity.BOTTOM);

//		mSeekBarLight = new SeekBar(getActivity());
//		mSeekBarLight.setBackgroundColor(Color.CYAN);
//		mSeekBarLight.setMax(1000);
//		mSeekBarLight.setPadding(0, 0, 0, 50);
//		mSeekBarLight.setProgress(100);
//		mSeekBarLight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//			@Override
//			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//				light1.setPower(progress/100f);
//			}
//
//			@Override
//			public void onStartTrackingTouch(SeekBar seekBar) {
//
//			}
//
//			@Override
//			public void onStopTrackingTouch(SeekBar seekBar) {
//
//			}
//		});
//		ll.addView(mSeekBarLight);

		mLayout.addView(ll);

		return mLayout;
	}

	private final class MultipleLightsRenderer extends AExampleRenderer {

		public MultipleLightsRenderer(Context context, @Nullable AExampleFragment fragment) {
			super(context, fragment);
		}

        @Override
		protected void initScene() {
			light1 = new PointLight(); //
			light1.setPower(1.5f);
			light1.setY(2.0);
			light1.setX(1.0);
			PointLight light2 = new PointLight();
			light2.setPower(0f);

			getCurrentScene().addLight(light1);
			getCurrentScene().addLight(light2);

			getCurrentCamera().setPosition(0, 2, 4);
			getCurrentCamera().setLookAt(0, 0, 0);

			try {
                final LoaderAWD parser = new LoaderAWD(mContext.getResources(), mTextureManager, R.raw.awd_suzanne);
                parser.parse();

//				Sphere rootSphere = new Sphere(.2f, 12, 12);
//				suzanne = rootSphere.clone(false);

                suzanne = parser.getParsedObject();
				Material material = new Material();
				material.setDiffuseMethod(new DiffuseMethod.Lambert());
                material.setColor(0xff990000);
				material.enableLighting(true);
				suzanne.setMaterial(material);

				getCurrentScene().addChild(suzanne);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Animation3D anim = new TranslateAnimation3D(
					new Vector3(-10, -10, 5), new Vector3(-10, 10, 5));
			anim.setDurationMilliseconds(4000);
			anim.setRepeatMode(Animation.RepeatMode.REVERSE_INFINITE);
			anim.setTransformable3D(light1);
			getCurrentScene().registerAnimation(anim);
			//anim.play();

			anim = new TranslateAnimation3D(new Vector3(10, 10, 5),
					new Vector3(10, -10, 5));
			anim.setDurationMilliseconds(2000);
			anim.setRepeatMode(Animation.RepeatMode.REVERSE_INFINITE);
			anim.setTransformable3D(light2);
			getCurrentScene().registerAnimation(anim);
			//anim.play();
		}

	}
}
