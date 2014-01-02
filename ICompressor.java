
public class ICompressor {
	//global variables
	int subArr[][];//the subsampled matrix of size m/2 *n/2 but it should be multiple of 8*8 so it may have to be padded
	int size=8;
	double [][]cosMatrix=new double[size][size];//used for the DCT
	double [][] TcosMatrix= new double [size][size];//used for the DCT
	int [][] lumaQ= new int [size][size];//the luminance  Quantization Matrix
	int[][] chromaQ= new int[size][size];//the chrominance Quantization Matrix
	int[][] subSChannel= new int[size][size];
	
	//constructor for the DCT
	ICompressor(){
		//make the 8x8 Matrix with the cosine Values 
		// and also make the Transpose of it so they are ready 
		startCosMatrixes();// start the cosine and the inverse cosine  Matrix
		startChromaQM();// start the Chroma Quantization Matrix for U and V
		startLumaQM();//start the Luma Quantization Matrix for Y
	}
	//the whole image is subsampled
	public int[][] chromaSub (int[][]origArray,int newWidth,int newHeight){
		boolean padColumn= false;//increase the width to make it multiple of 8
		boolean padRow=false;//increase the height to make it multiple of 8
		int deltaWidth = 0;
		int deltaHeight = 0;
		int x=0;int y=0;
		/*if(newWidth%8!=0){
			int temp=newWidth;
			newWidth=(int) (Math.ceil(newWidth/8.0) *8);
			deltaWidth= newWidth-temp;//make sure that the subArray is multiple of 8
			padColumn=true;//increase the width
		
		if(newHeight%8!=0){
			int temp=newHeight;
			newHeight=(int) (Math.ceil(newHeight/8.0) *8);//make sure that the subArray is multiple of 8
			deltaHeight= newHeight- temp;
			padRow=true;//increase the height. 
		}}*/
		subArr= new int[newWidth][newHeight];System.out.println( "newWidth: "+ newWidth + " newHeight :" + newHeight);
		//4:2:0 subsampling
		/*System.out.println("original array");
		for(x=0; x<width;x++){
			for(y=0;y<height;y++){
				
				System.out.print(origArray[x][y]+ " ");
				}
			System.out.println();
			}
		System.out.println("subsampled aray");*/
		for(x=0; x<newWidth;x=x+2){
			for(y=0;y<newHeight;y=y+2){
				subArr[x][y]=origArray[x][y];	//System.out.print(subArr[x/2][y/2]+ " ");
				subArr[x][y+1]=origArray[x][y];
				subArr[x+1][y]=origArray[x][y];
				subArr[x+1][y+1]=origArray[x][y];
			}//end width
			//System.out.println();
		}//end height
		
		if(padColumn){//copy the last column padding with the values of the previous not empty column
			System.out.println("had to pad Columns " + newWidth+ " and added " + deltaWidth + " to original sieze " + (newWidth-deltaWidth));
			/*for(x=0; x<newWidth;x++){
				for(y=(height/2)+1;y<newHeight;y++){
				subArr[x][y]=subArr[x][y-1];	
				}
			}*/
		}
		else if(padRow){//copy the last row padding with the values of the previous not empty column
			System.out.println("had to pad Rows " + newHeight + " and added " + deltaHeight + " to original sieze " + (newHeight-deltaHeight));
			/*for(x=(width/2)+1; x<newWidth;x++){
				for(y=0;y<newHeight;y++){
				subArr[x][y]=subArr[x-1][y];
				}
			}*/
		}
		else if (padRow && padColumn){//copy the last column and last row
			System.out.println("had to pad Rows  and Columns" + newHeight + " and added " + deltaHeight + " to original sieze " + (newHeight-deltaHeight));
			/*for(x=0; x<newWidth;x++){
				for(y=(height/2)+1;y<newHeight;y++){
				subArr[x][y]=subArr[x][y-1];
				}
			}
			for(x=(width/2)+1; x<newWidth;x++){
				for(y=0;y<newHeight;y++){
				subArr[x][y]=subArr[x-1][y];
				}
			}*/
			
		}
		else{System.out.println("image width and height were multiples of 8");}
		/*for(x=0; x<newWidth;x++){
			for(y=(height/2);y<newHeight;y++){
				System.out.print(x + ","+ y+": ");System.out.print(subArr[x][y]);System.out.print(" " );}System.out.println();}
		for(x=(width/2); x<newWidth;x++){
			for(y=0;y<newHeight;y++){
				System.out.print(x + ","+ y+": ");System.out.print(subArr[x][y]);System.out.print(" " );}System.out.println();}*/
		return subArr;
	}//end subsample
	
	
	public void startCosMatrixes(){
		/*Initialize the cosineMatrix when i=0 *1/2sqrt8 for all the first row */
		for(int j=0; j<size;j++){
			cosMatrix[0][j]=1.0/Math.sqrt((double)(size));
		/*Initialize the TcosineMatrix the transpose of cosMatrix */
			TcosMatrix[j][0]=cosMatrix[0][j];	
		}
		/*Now fill out the remaining of cosMatrix and TcosMatrix*/
		for(int i=1;i<size;i++){
			for(int j =0;j<size;j++){
				cosMatrix[i][j]=Math.sqrt(2.0/8.0) * Math.cos(((2.0 * (double)j + 1.0) * (double)i * Math.PI) / (2.0 * 8.0));
				TcosMatrix[j][i]=cosMatrix[i][j];		
			}	
		}	
	}//end startCosMatrixes	
	
	public int [][] performDCT(int [][] inArray,String type){
		int[][] result= new int[size][size];
		double[][] tempArr= new double[size][size];
		int i,j,k;
		double val;
		/*matrix multiplication is not commutative but 
		 * it is associative. Multiply cosMatrix chroma TcosMatrix
		 * like T*f(ij)*T'                                */
		
		//tempArry= f(i,j)*T'
		
		for (i = 0; i < size; i++){//for each row
        
            for (j = 0; j < size; j++)//for each column
            {
                tempArr[i][j] = 0.0;// so we can do the actual matrix multiplication
                for (k = 0; k < size; k++)
                {	
                	if(type.equals("y")){
                		inArray[i][k]=inArray[i][k]-128;
                	}
                    tempArr[i][j] += ((int)(inArray[i][k]) * TcosMatrix[k][j]);
                }
            }
        }//end first 1DCT
		
		//result= cosMatrix*tempArray
		 for (i = 0; i < size; i++){
	        
	            for (j = 0; j < size; j++)
	            {
	                val = 0.0;//add up all the values here for the corresponding coordinate of  F(uv)
	                for (k = 0; k < size; k++)
	                {
	                    val += (cosMatrix[i][k] * tempArr[k][j]);
	                }
	                result[i][j]=(int)Math.round(val);
	            } 
	        }//end second 1DCT	
		return result;	
	}//end DCT
	
	public void startLumaQM(){
		lumaQ[0][0]=16;lumaQ[0][1]=11;lumaQ[0][2]=10;lumaQ[0][3]=16;lumaQ[0][4]=24;lumaQ[0][5]=40;lumaQ[0][6]=51;lumaQ[0][7]=61;
		lumaQ[1][0]=12;lumaQ[1][1]=12;lumaQ[1][2]=14;lumaQ[1][3]=19;lumaQ[1][4]=26;lumaQ[1][5]=58;lumaQ[1][6]=60;lumaQ[1][7]=55;
		lumaQ[2][0]=14;lumaQ[2][1]=13;lumaQ[2][2]=16;lumaQ[2][3]=24;lumaQ[2][4]=40;lumaQ[2][5]=57;lumaQ[2][6]=69;lumaQ[2][7]=56;
		lumaQ[3][0]=14;lumaQ[3][1]=17;lumaQ[3][2]=22;lumaQ[3][3]=29;lumaQ[3][4]=51;lumaQ[3][5]=87;lumaQ[3][6]=80;lumaQ[3][7]=62;
		lumaQ[4][0]=18;lumaQ[4][1]=22;lumaQ[4][2]=37;lumaQ[4][3]=56;lumaQ[4][4]=68;lumaQ[4][5]=109;lumaQ[4][6]=103;lumaQ[4][7]=77;
		lumaQ[5][0]=24;lumaQ[5][1]=35;lumaQ[5][2]=55;lumaQ[5][3]=64;lumaQ[5][4]=81;lumaQ[5][5]=104;lumaQ[5][6]=113;lumaQ[5][7]=92;
		lumaQ[6][0]=49;lumaQ[6][1]=64;lumaQ[6][2]=78;lumaQ[6][3]=87;lumaQ[6][4]=103;lumaQ[6][5]=121;lumaQ[6][6]=120;lumaQ[6][7]=101;
		lumaQ[7][0]=72;lumaQ[7][1]=92;lumaQ[7][2]=95;lumaQ[7][3]=98;lumaQ[7][4]=112;lumaQ[7][5]=100;lumaQ[7][6]=103;lumaQ[7][7]=99;
	}
	public void startChromaQM (){
		chromaQ[0][0]=17;chromaQ[0][1]=18;chromaQ[0][2]=24;chromaQ[0][3]=47;chromaQ[0][4]=99;chromaQ[0][5]=99;chromaQ[0][6]=99;chromaQ[0][7]=99;
		chromaQ[1][0]=18;chromaQ[1][1]=21;chromaQ[1][2]=26;chromaQ[1][3]=66;chromaQ[1][4]=99;chromaQ[1][5]=99;chromaQ[1][6]=99;chromaQ[1][7]=99;
		chromaQ[2][0]=24;chromaQ[2][1]=26;chromaQ[2][2]=56;chromaQ[2][3]=99;chromaQ[2][4]=99;chromaQ[2][5]=99;chromaQ[2][6]=99;chromaQ[2][7]=99;
		chromaQ[3][0]=47;chromaQ[3][1]=66;chromaQ[3][2]=99;chromaQ[3][3]=99;chromaQ[3][4]=99;chromaQ[3][5]=99;chromaQ[3][6]=99;chromaQ[3][7]=99;
		chromaQ[4][0]=99;chromaQ[4][1]=99;chromaQ[4][2]=99;chromaQ[4][3]=99;chromaQ[4][4]=99;chromaQ[4][5]=99;chromaQ[4][6]=99;chromaQ[4][7]=99;
		chromaQ[5][0]=99;chromaQ[5][1]=99;chromaQ[5][2]=99;chromaQ[5][3]=99;chromaQ[5][4]=99;chromaQ[5][5]=99;chromaQ[5][6]=99;chromaQ[5][7]=99;
		chromaQ[6][0]=99;chromaQ[6][1]=99;chromaQ[6][2]=99;chromaQ[6][3]=99;chromaQ[6][4]=99;chromaQ[6][5]=99;chromaQ[6][6]=99;chromaQ[6][7]=99;
		chromaQ[7][0]=99;chromaQ[7][1]=99;chromaQ[7][2]=99;chromaQ[7][3]=99;chromaQ[7][4]=99;chromaQ[7][5]=99;chromaQ[7][6]=99;chromaQ[7][7]=99;
	}
	
	public int[][] quantize(int[][]channel, String type,int q ){
		//Have to make a quantization in 8x8 channels, and use the corresponding Quatization matrix for Luma or Chrominace
		int[][] result= new int[size][size];
			
			//perform quantization for the chroma channels
			if((type.equals("u"))||(type.equals("v"))){
		
				for(int i=0;i<size;i++){
					for(int j=0;j<size;j++){
						result[i][j]=(int)Math.round((float)(channel[i][j])/(q*chromaQ[i][j]));//round and cast to int the result of each ij component
		
					}
				}
			}
			
			//perform quantization for the luma component Y
			else
				for(int i=0;i<size;i++){
					for(int j=0;j<size;j++){
						result[i][j]=(int)Math.round((float)(channel[i][j])/(q*lumaQ[i][j]));//round and cast to int the result of each ij component
					}
				}
		
		return result;//either return chroma U,V after quantization or the luma Y one
	} //end quantizise
	
	public int[][] performIDCT(int[][] inArray,String type){
		int[][] result= new int[size][size];
		double[][] tempArr= new double[size][size];
		double val;
		int i, j ,k;
		//f(i,j) = T^T * F(u,v) * T.  Note, the places for T and T^T (T-transpose) are switched.
		for (i=0; i<size; i++)
        {
            for (j=0; j<size; j++)
            {
                tempArr[i][j] = 0.0;//tempArray  =F(u,v) * T

                for (k=0; k<size; k++)
                {	
                    tempArr[i][j] += inArray[i][k] * cosMatrix[k][j];
                }
            }
        }
		//result=T^T*TempArray
		 for (i=0; i<size; i++)
	        {
	            for (j=0; j<size; j++)
	            {
	                val = 0.0;

	                for (k=0; k<size; k++)
	                {
	                    val += TcosMatrix[i][k] * tempArr[k][j]; 
	                }
	               
	                result[i][j]=(int)Math.round(val); 
	                if(type.equals("y")){
	                	result[i][j]=result[i][j]+128;
	                }
	            }
	        }  
		 return  result;
	}//end performIDCT
	
	public int[][] dequantize(int[][]channel, String type,int q){
		//Have to make a quantization in 8x8 channels, and use the corresponding Quatization matrix for Luma or Chrominace
				int[][] result= new int[size][size];
					
					//perform quantization for the chroma channels
					if((type.equals("u"))||(type.equals("v"))){
				
						for(int i=0;i<size;i++){
							for(int j=0;j<size;j++){
								result[i][j]=(int)Math.round((float)(channel[i][j])*(q*chromaQ[i][j]));//round and cast to int the result of each ij component
				
							}
						}
					}
					
					//perform quantization for the luma component Y
					else
						for(int i=0;i<size;i++){
							for(int j=0;j<size;j++){
								result[i][j]=(int)Math.round((float)(channel[i][j])*(q*lumaQ[i][j]));//round and cast to int the result of each ij component
							}
						}
				
				return result;//either return chroma U,V after quantization or the luma Y one
			}

	
	

}//end class
