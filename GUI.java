import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField;
import  java.text.NumberFormat; 
import java.io.File;
import java.io.IOException;


public class GUI extends JFrame implements ActionListener  {
	//global variables
	JPanel mainPanel,buttonPanel,inSizePanel,inleftPanel;
	IMGPanel inImagePanel,outImagePanel;
	JButton browseButton,convertButton,saveButton;
	JTextField qualityText;
	JLabel chooseQ,inImagesize,outImagesize;
	BufferedImage inImage, outImage;
	JComboBox coptions;
	String[] conversions ={"      RGB->Y","      RGB->U","      RGB->V","Image Compression"};
	int[][] redArray=null;   int[][] newRedArray=null;
	int[][] greenArray=null; int[][] newGreenArray=null;
	int[][] blueArray=null;  int[][] newBlueArray=null;
	int[][] yArray=null;
	int[][] uArray=null;
	int[][] vArray=null;
	int newWidth;//new width if clipping was applied
	int newHeight;//new height if clipping was applied
	int inFsize, outFsize;
	final JFileChooser fc = new JFileChooser();
	
	
	//constructor
	public GUI(){
	
	mainPanel = new JPanel(null);
	add(mainPanel);
	inleftPanel= new JPanel(new GridLayout(2,1,15, 20)); 
	inImagePanel= new IMGPanel();
	inImagePanel.setLocation(60, 40);
	inImagePanel.setSize(500,400);
	inImagesize=new JLabel();
	inSizePanel=new JPanel();
	inSizePanel.add(inImagesize);
	//inSizePanel.setLocation(90, 420);
	//inSizePanel.setVisible(false);
	inleftPanel.add(inImagePanel);
	inleftPanel.add(inSizePanel);
	
	
	//panel for the buttons
	buttonPanel=new JPanel(new GridLayout(6,1,15, 20));
	buttonPanel.setLocation(620, 60);
	buttonPanel.setSize(130, 300);
	
	
	//add buttons and dropdown to coresponding panel
	
	browseButton=new JButton(" Browse");
	browseButton.setLocation(0, 0);
	browseButton.setSize(100,40);
	browseButton.addActionListener(this);
	
	coptions=new JComboBox(conversions);
	coptions.addActionListener(this);
	coptions.setLocation(0, 60);
	coptions.setSelectedIndex(0);
	
	
	
	
	convertButton = new JButton("Convert");
	convertButton.setLocation(0, 100);
	convertButton.setSize(110,40);
	convertButton.addActionListener(this);
	
	saveButton= new JButton("Save");
	saveButton.addActionListener(this);
	
	chooseQ=new JLabel("JPEG quality:");
	chooseQ.setVisible(true);
	chooseQ.setSize(80, 10);
	
	qualityText=new JTextField();
	qualityText.setSize(50, 10);
	qualityText.setVisible(true);
	//Code Found Online so the text field only accepts  numbers
	qualityText.addKeyListener(new KeyAdapter() 
	{ 
	public void keyTyped(KeyEvent ke) 
	{ 
	char c = ke.getKeyChar(); 
	if((!(Character.isDigit(c))||(c == KeyEvent.VK_BACK_SPACE) || 
			(c == KeyEvent.VK_DELETE))) // Only digits  
	{ 
	getToolkit().beep();	
	ke.consume(); 
	} 
	} 
	public void keyReleased(KeyEvent e){} 
	public void keyPressed(KeyEvent e){} 
	});
	
	buttonPanel.add(browseButton);
	buttonPanel.add(coptions);
	buttonPanel.add(convertButton);
	buttonPanel.add(chooseQ);
	buttonPanel.add(qualityText);
	buttonPanel.add(saveButton);
	
	outImagePanel=new IMGPanel();
	outImagePanel.setLocation(775, 40);
	outImagePanel.setSize(500,400);
	outImagesize= new JLabel();
	outImagesize.setLocation(800, 420);
	
	
	//adding to the mainPanel to be displayed
	mainPanel.add(inImagePanel);
	mainPanel.add(inleftPanel);
	mainPanel.add(buttonPanel);
	mainPanel.add(outImagePanel);
	mainPanel.add(outImagesize);
	
	}
	
	
	
	
	
	@Override
	public void actionPerformed(ActionEvent act) {
		int w;//width of inImage
		int h;//height of inImage
		
		
		//DELETE THIS LATER!!!
		//try{inImage=ImageIO.read(new File("C://Users//Owner//Pictures//Caro//te extraño.jpg"));} catch(Exception e){}
		//Here
		
		
		//inImagePanel.setBufferedImage(inImage);		
		//action for browse
		if(act.getSource()==browseButton){
			fc.addChoosableFileFilter(new ImageFilter());
        	fc.setAcceptAllFileFilterUsed(false);
			int returnVal=fc.showOpenDialog(GUI.this);
			if(returnVal==JFileChooser.APPROVE_OPTION){
				File file = fc.getSelectedFile();
				long fileSizeInBytes = file.length();
				// Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
				long fileSizeInKB = fileSizeInBytes / 1024;
				// Convert the KB to MegaBytes (1 MB = 1024 KBytes)
				long fileSizeInMB = fileSizeInKB / 1024;
				inFsize=(int) fileSizeInKB;
				System.out.println("in file: " +inFsize );
				
				
				try{
					inImage=ImageIO.read(file);
					inImagePanel.setBufferedImage(inImage);					
				}
				catch(Exception e){
					e.printStackTrace();
				}	
			}			
		}
		
		else if (act.getSource()==convertButton){
			 if(inImage==null){
				 JOptionPane.showMessageDialog(null, "Please Select an image first");
				 return;
			 }
			 w = inImage.getWidth(null);
			 h= inImage.getHeight(null);
			 
			int slopt = coptions.getSelectedIndex();//the selected option
			
			int []inputVal= new int[h*w];
			int[] YVal= new int[h*w];// for the options in dropdown menu 0 1 2
			int[] UVal= new int[h*w];
			int[] VVal= new int[h*w];
			int[] totalVal= new int [h*w];
			int red,green, blue; 
			WritableRaster raster;
			PixelGrabber grabber = new PixelGrabber(inImage.getSource(),0,0,w,h,inputVal,0,w);
			 try {
				 grabber.grabPixels();
				 
			 }
			catch(Exception e){
				e.printStackTrace();
				JComboBox cb= (JComboBox)act.getSource();
			}
					if(slopt==0){
						//rgb to y
				            for (int index = 0; index < h * w; ++index){
				            	red = ((inputVal[index] & 0x00ff0000) >> 16);
				            	green =((inputVal[index] & 0x0000ff00) >> 8);
				            	blue = ((inputVal[index] & 0x000000ff) );
				            	YVal[index] = (int)((0.299 * (float)red) + (0.587 * (float)green) + (0.114 * (float)blue));  
				            }
				            // write Y values to the output image
				            outImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
				        	raster = (WritableRaster) outImage.getData();
				        	raster.setPixels(0, 0, w, h, YVal);
				        	outImage.setData(raster);
				        	outImagePanel.setBufferedImage(outImage);	 
						}
						
						
					
					else if (slopt==1){
						//rgb to u
						 for (int index = 0; index < h * w; ++index){
				            	red = ((inputVal[index] & 0x00ff0000) >> 16);
				            	green =((inputVal[index] & 0x0000ff00) >> 8);
				            	blue = ((inputVal[index] & 0x000000ff) );
				               	UVal[index]=(int)((-0.14713 *(float)red) + (-0.28886 * (float)green));			            
				            }
				            // write Y values to the output image
				            outImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
				        	raster = (WritableRaster) outImage.getData();
				        	raster.setPixels(0, 0, w, h, UVal);
				        	outImage.setData(raster);
				        	outImagePanel.setBufferedImage(outImage);						
					}
					
					else if(slopt==2){
						//rgb to v
						 for (int index = 0; index < h * w; ++index){
				            	red = ((inputVal[index] & 0x00ff0000) >> 16);
				            	green =((inputVal[index] & 0x0000ff00) >> 8);
				            	blue = ((inputVal[index] & 0x000000ff) );
				            	VVal[index]=(int)(0.615 *(float)(red)+ (-0.51449 * (float)(red)) +(-0.10001*(float)blue));
				            }
				            // write Y values to the output image
				            outImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
				        	raster = (WritableRaster) outImage.getData();
				        	raster.setPixels(0, 0, w, h, VVal);
				        	outImage.setData(raster);
				        	outImagePanel.setBufferedImage(outImage);
					}
					
					else{       
								if(qualityText.getText().trim().length()==0){
									JOptionPane.showMessageDialog(null, "Make sure to enter quality compression");
									return;
								}
									
							
								
								int QQ= Integer.parseInt(qualityText.getText());//Quality of Quantization
								redArray= getArray(inImage,w,h,"red");
								greenArray=getArray(inImage,w,h,"green");
								blueArray=getArray(inImage,w,h,"blue");
								//from here the image may have a newWidth and/or newHeight
								yArray= converttoYUVArray(redArray,greenArray,blueArray,"y",newWidth,newHeight);
								uArray=converttoYUVArray(redArray,greenArray,blueArray,"u",newWidth,newHeight);
								vArray=converttoYUVArray(redArray,greenArray,blueArray,"v",newWidth,newHeight);
								ICompressor mycomp= new ICompressor();
								//subsample the corresponding U and V channels except Y
								int[][] Usubsampled=mycomp.chromaSub(uArray, newWidth, newHeight);
								int[][] Vsubsampled=mycomp.chromaSub(vArray, newWidth, newHeight);
								
								/*here is the more complicated group of for loops, read the whole subsampled image
								 * in block of 8x8 and then to each one apply DCT quantization in order to compress.
								 * And then start to do the reverse in order to recover the image.The following steps have to
								 * be done for each YUV channel */
								//this variables will hold the 8x8 blocks of the subsampled UV and the original Y channel
								int[][]preDCTedYblock= new int[8][8]; int[][] preDCTedUblock=new int[8][8]; int[][] preDCTedVblock= new int[8][8];
								//to the previous blocks DCT will be applied
								int[][]DCTedYblock= new int[8][8]; int[][] DCTedUblock=new int[8][8]; int[][] DCTedVblock= new int[8][8];
								//to the previous block Quantization will be applied
								int[][]QedYblock= new int[8][8]; int[][] QedUblock=new int[8][8]; int[][] QedVblock= new int[8][8];
								//the result of a newWidth by newHeight fully compressed Channel
								int[][]compY=new int[newWidth][newHeight]; int[][]compU=new int[newWidth][newHeight];int[][]compV=new int[newWidth][newHeight];
								
								for(int x=0;x<newWidth/8;x++){
									for(int y=0; y<newHeight/8;y++){
										for(int i=0;i<8;i++){
											for(int j= 0;j<8;j++){
												preDCTedYblock[i][j]= yArray[x*8+i][y*8+j];
												preDCTedUblock[i][j]= Usubsampled[x*8 +i][y*8+j];
												preDCTedVblock[i][j]= Vsubsampled[x*8 +i][y*8+j];
												}
											}//end of reading a block
											//DCT to each block
											DCTedYblock= mycomp.performDCT(preDCTedYblock,"y");
											DCTedUblock= mycomp.performDCT(preDCTedUblock,"u");
											DCTedVblock= mycomp.performDCT(preDCTedVblock,"v");
											//Quantize each block
											QedYblock=mycomp.quantize(DCTedYblock, "y",QQ);
											QedUblock=mycomp.quantize(DCTedUblock, "u",QQ);
											QedVblock=mycomp.quantize(DCTedVblock, "v",QQ);
											//build a fully compressed channels from the 8x8 blocks
											for(int i=0; i<8;i++){
												for(int j=0;j<8;j++){
													compY[x*8+i][y*8+j]=QedYblock[i][j];
													compU[x*8+i][y*8+j]=QedUblock[i][j];
													compV[x*8+i][y*8+j]=QedVblock[i][j];
												}
											}												
										}
									}
								/*The following steps have to be done for each YUV channel  in order to uncompress the image  and restore it
								 * its kind of the code above but things are done in reverse order starting with compY compU compV*/
								//just like before the 8x8 predequantized block 
								int[][] preDQYblock= new int[8][8]; int[][] preDQUblock= new int[8][8]; int[][] preDQVblock= new int[8][8];
								// to the previous block dequantization will be applied
								int[][] deqYblock= new int[8][8]; int[][] deqUblock= new int[8][8]; int[][] deqVblock= new int[8][8];
								// to the previous block IDCT will be applied
								int[][] iDCTedYblock= new int[8][8]; int[][] iDCTedUblock= new int[8][8]; int[][] iDCTedVblock= new int[8][8];
								//the result of a newWidth by newHeight fully UNcompressed Channel
								int[][] uncompY = new int[newWidth][newHeight]; int[][] uncompU = new int[newWidth][newHeight];int[][] uncompV = new int[newWidth][newHeight];
								
								
								for(int x=0;x<newWidth/8;x++){
									for(int y=0; y<newHeight/8;y++){
										for(int i=0;i<8;i++){
											for(int j= 0;j<8;j++){
												preDQYblock[i][j]= compY[x*8 +i][y*8+j];
												preDQUblock[i][j]= compU[x*8 +i][y*8+j];
												preDQVblock[i][j]= compV[x*8 +i][y*8+j];
												
												}
											}//end of reading a block
											//Dequantize each block
											deqYblock= mycomp.dequantize(preDQYblock, "y",QQ);
											deqUblock= mycomp.dequantize(preDQUblock, "u",QQ);
											deqVblock= mycomp.dequantize(preDQVblock, "v",QQ);
											//Inverse DCT each block
											iDCTedYblock=mycomp.performIDCT(deqYblock,"y");
											iDCTedUblock=mycomp.performIDCT(deqUblock,"u");
											iDCTedVblock=mycomp.performIDCT(deqVblock,"v");
											//build a fully UNcompressed channels from the 8x8 blocks
											for(int i=0; i<8;i++){
												for(int j=0;j<8;j++){
													uncompY[x*8+i][y*8+j]=iDCTedYblock[i][j];
													uncompU[x*8+i][y*8+j]=iDCTedUblock[i][j];
													uncompV[x*8+i][y*8+j]=iDCTedVblock[i][j];
												}
											}												
										}
									}//end Uncompressing
								
								System.out.println();
								System.out.println(" the uncompressed in YUV");
								for(int i=0; i<16;i++){
									for(int j=0;j<16;j++){
										System.out.print(vArray[i][j] + " ");
										
										}
									System.out.println();
									}
								System.out.println();
								System.out.println(" the compressed");
								for(int i=0; i<16;i++){
									for(int j=0;j<16;j++){
										System.out.print(uncompV[i][j] + " ");
										
										}
									System.out.println();
									}
								System.out.println();
								newRedArray=converttoRGBArray(uncompY,uncompU,uncompV,"r",newWidth,newHeight);
								newGreenArray=converttoRGBArray(uncompY,uncompU,uncompV,"g",newWidth,newHeight);
								newBlueArray=converttoRGBArray(uncompY,uncompU,uncompV,"b",newWidth,newHeight);
								
								System.out.println();
								System.out.println(" the uncompressed in RGB");
								for(int i=0; i<16;i++){
									for(int j=0;j<16;j++){
										System.out.print(greenArray[i][j] + " ");
										
										}
									System.out.println();
									}
								System.out.println();
								System.out.println(" the compressed");
								for(int i=0; i<16;i++){
									for(int j=0;j<16;j++){
										System.out.print(newGreenArray[i][j] + " ");
										
										}
									System.out.println();
									}
									
								int i=0;
								//create the new colors that will compose the image
								int[] newVVal=new int[newWidth*newHeight];
								System.out.println("width "+ newWidth + "height "+newHeight );
									for(int y=0;y<newHeight;y++){//for some strange reason have to read the image starting with the y axis then the x one..
										for(int x=0;x<newWidth;x++){// 4 hours figuring out this ....
											Color color= new Color(newRedArray[x][y],newGreenArray[x][y],newBlueArray[x][y]);
											newVVal[i]=color.getRGB();
											//newVVal[i]=-16777216 | (newRedArray[x][y] << 16) | (newGreenArray[x][y] << 8) | newBlueArray[x][y];
											//System.out.println("x :"+ x + "y: "+y);
											i++;
										}
									}
								

								
								
								outImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
					        	//raster = (WritableRaster) outImage.getData();
					        	//raster.setPixels(0, 0, newWidth, newHeight, newVVal);
					        	//outImage.setData(raster);
								outImage.setRGB(0, 0, newWidth, newHeight, newVVal, 0, newWidth);
					        	outImagePanel.setBufferedImage(outImage);
								int[][]lena={ {70,70, 100,70,87,87,150,187},
											  {85,100,96,79,87,154,87,113},
											  {100,85,116,79,70,87,86,196},
											  {136,69,87,200,79,71,117,96},
											  {161,70,87,200,103,71,96,113},
											  {161,123,147,133,113,113,85,161},
											  {146,147,175,100,103,103,163,187},
											  {156,146,189,70,113,161,163,197}};
								System.out.println("---Lena---");
								for(int x=0;x<8;x++){
									for(int y=0;y<8;y++){
										System.out.print(lena[x][y]+" ");
										}
									System.out.println();
									}
								int[][] lenaD= mycomp.performDCT(lena,"y");
								System.out.println("---LenaDCT---");
								for(int x=0;x<8;x++){
									for(int y=0;y<8;y++){
										System.out.print(lenaD[x][y]+" ");
										}
									System.out.println();
									}
								int[][] lenaQ=mycomp.quantize(lenaD, "y",QQ);
								System.out.println("---LenaQuantized---");
								for(int x=0;x<8;x++){
									for(int y=0;y<8;y++){
										System.out.print(lenaQ[x][y]+" ");
										}
									System.out.println();
									}
								int[][] lenaDQ=mycomp.dequantize(lenaQ, "y",QQ);
								System.out.println("---LenaDEQuantized---");
								for(int x=0;x<8;x++){
									for(int y=0;y<8;y++){
										System.out.print(lenaDQ[x][y]+" ");
										}
									System.out.println();
									}
								int[][]newlena=mycomp.performIDCT(lenaDQ,"y");
								System.out.println("---newLena---");
								for(int x=0;x<8;x++){
									for(int y=0;y<8;y++){
										System.out.print(newlena[x][y]+" ");
										}
									System.out.println();
									}
								System.out.println("RGB");
								for(int x=0;x<8;x++){
									for(int y=0;y<8;y++){
										System.out.print(redArray[x][y]+" ");
										}
									System.out.println();
									}
								System.out.println("RGB to YUV");
								int[][] tempy=converttoYUVArray(redArray,greenArray,blueArray,"y",8,8);
								for(int x=0;x<8;x++){
									for(int y=0;y<8;y++){
										System.out.print(tempy[x][y]+" ");
										}
									System.out.println();
									}
								System.out.println("YUV to RGB");
								int[][] tempr=converttoRGBArray(yArray,uArray,vArray,"r",8,8);
								for(int x=0;x<8;x++){
									for(int y=0;y<8;y++){
										System.out.print(tempr[x][y]+ " ");
										}
									System.out.println();
									}
								/*for(int x=0;x<newWidth;x++){
									for(int y=0;y<newHeight;y++){
									outImage.setRGB(x, y, newVVal[x]);	
									}
								}*/
								
								
								
					}//end else
								
								/*for(int i=0;i<8;i++){
									for(int j=0;j<8;j++){
										System.out.print(yArray[i][j]+ " ");
									}
									System.out.println();
								}

								//perform DCT to YUV channels
								int[][] YDCted= mycomp.performDCT(yArray);
								int[][] UDCTed= mycomp.performDCT(Usubsampled);
								int[][] VDCTed=mycomp.performDCT(Vsubsampled);
								//quantize on the YUV channels
								int [][] quantY=mycomp.quantize(YDCted, "y");
								int [][] quantU=mycomp.quantize(UDCTed, "u");
								int [][] quantV=mycomp.quantize(VDCTed, "v");
								//decompress back the image
								int[][] deQY= mycomp.dequantize(quantY, "y");
								int[][] invYDCTed= mycomp.performIDCT(deQY);
								for(int i=0;i<8;i++){
									for(int j=0;j<8;j++){
										System.out.print(invYDCTed[i][j]+ " ");
									}
									System.out.println();
								}
								System.out.println();
								int err=0;
								int [][]errM= new int[8][8];
								for(int i=0;i<8;i++){
									for(int j=0;j<8;j++){
										err+=Math.abs(quantY[i][j]- deQY[i][j]);
										errM[i][j]=Math.abs(quantY[i][j]- deQY[i][j]);
									}
									System.out.println();
								}
								System.out.println("error of  "+ err/64);*/
								/*convert to yuv
								 * perform subsambpling
								 * perform quntization
								 * dct
								 * idct
								 * rebuild 
								 * */							 
				            /* write Y values to the output image 
							outImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
				        	raster = (WritableRaster) outImage.getData();
				        	raster.setPixels(0, 0, w, h, totalVal);
				        	outImage.setData(raster);
				        	outImagePanel.setBufferedImage(outImage);*/
					   	
        	 
		}//end convert Button
		else if(act.getSource() == saveButton){
        	if(outImage == null)
        		return;
        	fc.addChoosableFileFilter(new ImageFilter());
        	fc.setAcceptAllFileFilterUsed(false);
        	int returnVal = fc.showSaveDialog(GUI.this);
        	if (returnVal == JFileChooser.APPROVE_OPTION) {
        		File file = fc.getSelectedFile();
        		
        		try {
            	    ImageIO.write(outImage, "jpg", file);
            	   long fileSizeInBytes = file.length();
            	   // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
            	   long fileSizeInKB = fileSizeInBytes / 1024;
            	   // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
            	   long fileSizeInMB = fileSizeInKB / 1024;
            	   System.out.println("New File Size:"+ fileSizeInKB);
            	   outFsize=(int) fileSizeInKB;
            	   JOptionPane.showMessageDialog(null, "Image compressed from: "+ inFsize+ " KB" +" to: " + outFsize + " KB");
            	   
            	   
            	} catch (IOException e) {
            		e.printStackTrace();
            	}
        	}
        }
		
		
		
	}//end actionperformed	
	
	
	





	//Extra Functions
	//returns the specified 2D array of the buffered image
	public int[][] getArray(BufferedImage image, int width, int height, String color){
		newWidth=width;
		newHeight=height;
		if(newWidth%8!=0){
			newWidth=(int) (Math.floor(newWidth/8.0) *8);
		}
		if(newHeight%8!=0){
			newHeight=(int) (Math.floor(newHeight/8.0) *8);
		}
		int values[] = new int[newWidth * newHeight];
    	PixelGrabber grabber = new PixelGrabber(image.getSource(), 0, 0, width, height, values, 0, width);
    		    try
    		    {
    		        grabber.getPixels();
    		    }
    		    catch (Exception e) {e.printStackTrace();}

    	int result[][] = new int[newWidth][newHeight];
    	/*00 01 02 03
    	 *10 11 12 13 
    	 *20 21 22 23 
    	 *30 31 32 33
    	 * First index is for the rows, second index columns
    	 * Want to move first on the  columns, then on the rows
    	 * */
  		int index = 0;
    	for (int x = 0; x < newWidth; ++x)
    	{
    	    for (int y = 0; y < newHeight; ++y)
    	    {
    	    	int rgb=image.getRGB(x,y);
    	    	
    	    	
    	    	if(color.equals("red")){
    	    		result[x][y] = ((rgb & 0x00ff0000) >> 16);
    	    		index++;
        			}
    	    	else if (color.equals("green")){
    	    		result[x][y] = ((rgb & 0x0000ff00) >> 8);	    		
    	    		}
    	    	else{
    	    		result[x][y] = (rgb & 0x000000ff);
    	    		}
    	    }//end width
    	}//end height
    	return result;
    }//end getArray
		
	//returns the specified 2D array of the converted RGB to YUV	
	public int[][] converttoYUVArray(int[][] redArray, int[][] greenArray, int[][] blueArray, String newcolor,int width,int height) {
		 int result[][]=new int[width][height];
		 for(int x=0;x<width;x++){
			 for(int y=0;y<height;y++){
				 if(newcolor.equals("y")){
					 result[x][y]=(int)((0.299 * (float)redArray[x][y]) + (0.587 * (float)greenArray[x][y]) + (0.114 * (float)blueArray[x][y]));
							 }
				 else if (newcolor.equals("u")){
					 result[x][y]=(int)((-0.14713 *(float)redArray[x][y]) + (-0.28886 * (float)greenArray[x][y])+(0.436*(float)blueArray[x][y]));
				 }
				 else{
					 result[x][y]=(int)(0.615 *(float)(redArray[x][y])+ (-0.51449 * (float)(greenArray[x][y])) +(-0.10001*(float)blueArray[x][y])); 
				 }			 
			 }//end height
		 }//end width
		return result; 
	}//end converttoYUVArray	
	
	public int[][] converttoRGBArray(int[][] YArray, int[][] UArray, int[][] VArray, String newcolor,int newWidth,int newHeight) {
		 int result[][]=new int[newWidth][newHeight];
		 for(int x=0;x<newWidth;x++){
			 for(int y=0;y<newHeight;y++){
				 if(newcolor.equals("r")){
					 result[x][y]=(int) ((float)YArray[x][y]+ 1.13983*(float)VArray[x][y]);
					 if(result[x][y]<0){result[x][y]=0;}if(result[x][y]>255){result[x][y]=255;}
							 }
				 else if (newcolor.equals("g")){
					 result[x][y]=(int) ((float)YArray[x][y] -0.39465*(float)UArray[x][y]-0.58060*(float)VArray[x][y]);
					 if(result[x][y]<0){result[x][y]=0;}if(result[x][y]>255){result[x][y]=255;}
				 }
				 else{
					 result[x][y]=(int) ((float)YArray[x][y]+2.03211*(float)UArray[x][y]);
					 if(result[x][y]<0){result[x][y]=0;}if(result[x][y]>255){result[x][y]=255;}
				 }			 
			 }//end height
		 }//end width
		return result; 
	}//end convertArray		
	
	
	
	
	
}//end class
