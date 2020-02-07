import java.util.Arrays;
import java.util.Random;

public class AlphaCalculations {
	
	static int N = 4; 
	static int M = 27; 
	static int T = FileReader.readFromFile();
	
	static int minIterations = 200;
	
	static int iterations = 0;
	
	static double eps = 0.001;
	
	static double[][] B = new double[N][M];
	
	static double[][] A = new double[N][N];
	
	static double[] pi = new double[N];
	
	//static double A [][] = {{0.47468,0.52532},{0.51656,0.48344}};
	//static double B [][] =  {{0.03735,0.03408,0.03455,0.03828,0.03782,0.03922,0.03688,0.03408,0.03875,0.04062,0.03735,0.03968,0.03548,0.03735,0.04062,0.03595,0.03641,0.03408,0.04062,0.03548,0.03922,0.04062,0.03455,0.03595,0.03408,0.03408,0.03688},
	       // {0.03909,0.03408,0.03537,0.03909,0.03583,0.03630,0.04048,0.03537,0.03816,0.03909,0.03490,0.03723,0.03537,0.03909,0.03397,0.03397,0.03816,0.03676,0.04048,0.03443,0.03537,0.03955,0.03816,0.03723,0.03769,0.03955,0.03397}};
	
	//static double pi[] = {0.52316, 0.48684};
	
	static double prob = 0.0;
	
	static double[] scalingFactors = new double[T];
	
	static double[][] alpha = new double[T][N];
	
	static double[][] beta = new double[T][N];
	
	static double[][] gamma = new double[T][N];
	
	static double[][][] digamma = new double[T][N][N];
	
	static double oldProb = Double.NEGATIVE_INFINITY;
	
	public static void initializeA_B_Pi()
	{
		Random random  = new Random();
		
		//Initialize A Matrix
		for(int i = 0 ; i< N ; i++)
		{
			int num = 0;
			for(int j = 0 ; j < N ; j++)
			{
				int randomNumber = random.nextInt(100);
				num += randomNumber;
				A[i][j] = randomNumber;
			}
			
			for(int j = 0 ; j<N ; j++)
			{
				A[i][j] = A[i][j]/num;
			}
		}
		
		//Initialize B Matrix
		
		for(int i = 0 ; i<N ; i++)
		{
			int num = 0;
			
			for(int j = 0 ; j<M ; j++)
			{
				int randomNumber = random.nextInt(100);
				num += randomNumber;
				B[i][j] = randomNumber;
			}
			for(int j = 0 ; j<M ; j++)
			{
				B[i][j] = B[i][j]/num;
			}
		}
		
		//Initialize Pi Matrix
		int total = 0;
		
		for(int i = 0 ; i<N ; i++)
		{
			int randomNumber = random.nextInt(100);
			total += randomNumber;
			pi[i] = randomNumber;
		}
		for(int i = 0 ; i<N ; i++)
		{
			pi[i] = pi[i]/total;
		}
		
	}
	
	public static void initialPrint() {
		
		System.out.println("Initial A");
		System.out.println(Arrays.deepToString(A));
		System.out.println("Initial B");
		System.out.println(Arrays.deepToString(B));
		System.out.println("Initial pi");
		System.out.println(Arrays.toString(pi));
		
	}
	
	public static void calculateAlpha()
	{
		scalingFactors[0] = 0;
		for (int i = 0; i < N; i++) {
			
			alpha[0][i] =  pi[i] * B[i][FileReader.characterList.get(0)];
			scalingFactors[0] += alpha[0][i];
		}

		scalingFactors[0] = 1/scalingFactors[0];
		
		for (int i = 0; i < N; i++) {
		
			alpha[0][i] *= scalingFactors[0];
		}
		
		for(int t = 1 ; t < T ; t++)
		{
			scalingFactors[t] = 0;
			
			for(int i = 0 ; i < N ; i++)
			{
				alpha[t][i] = 0;
				
				for(int j = 0 ; j< N ; j++)
				{
					alpha[t][i] += alpha[t-1][j]*A[j][i];
				}
				alpha[t][i] *= B[i][FileReader.characterList.get(t)];
				scalingFactors[t] += alpha[t][i];
			}
			scalingFactors[t] = 1/scalingFactors[t];
			
			for(int index = 0 ; index< N ; index++)
			{
				alpha[t][index] *= scalingFactors[t];
			}
			
		}
	}
	
	public static void calculateBeta() {
		
		for(int i = 0; i < N ; i++) {
			beta[T-1][i] = scalingFactors[T-1];
		}
		
		for( int t = T - 2 ; t >= 0 ; t--) {
			
			for(int i = 0 ; i < N ; i++) {
				beta[t][i] = 0.0;
				
				for(int j = 0; j < N ; j++) {
					beta[t][i] += A[i][j]*beta[t+1][j]*B[j][FileReader.characterList.get(t+1)];
				}
				
				beta[t][i] *= scalingFactors[t];
				
			}
		}
	}
	
	public static void gammaCalculations() {
		
		for(int t = 0; t < T-1; t++) {
			
			double denom = 0.0;
			
			for(int i = 0; i < N; i++) {
				for(int j = 0; j < N ; j++) {
					denom += alpha[t][i]*A[i][j]*beta[t+1][j]*B[j][FileReader.characterList.get(t+1)];
				}
			}
			
			for(int i = 0; i < N; i++) {
				gamma[t][i] = 0.0;
				for(int j = 0 ; j < N; j++) {
						digamma[t][i][j] = (alpha[t][i]*A[i][j]*B[j][FileReader.characterList.get(t+1)]*beta[t+1][j])/denom;
						gamma[t][i] += digamma[t][i][j];
				}
			
			}
	
		}
		
		double denom = 0.0;
		for(int i = 0; i < N; i++) {
			denom += alpha[T-1][i];
		}
		
		for(int i = 0; i < N; i++) {
			gamma[T-1][i] = (alpha[T-1][i])/denom;
		}
		
		//System.out.println(Arrays.deepToString(gamma));
		
	}
	
	public static void reestimations() {
		
		for(int i = 0; i < N ;i++) {
			pi[i] = gamma[0][i];
		}
		
		for(int i =0 ; i < N; i++) {
			for(int j = 0; j < N ;j++) {
				double num = 0.0; 
				double denom = 0.0;
				
				for(int t = 0 ; t < T-1; t++) {
					num += digamma[t][i][j];
					denom += gamma[t][i];
				}
				
				A[i][j] = num/denom;
			}
		}
		
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < M ; j++) {
				double num = 0.0;
				double denom = 0.0;
				
				for(int t = 0; t < T; t++) {
					if(FileReader.characterList.get(t) == j)
					{
						num += gamma[t][i];
					}
					denom += gamma[t][i];
				}
				
				B[i][j] = num/denom;
			}
		}
		
	}
	
	static void computeProbability() {
		
		prob = 0;
		
		for(int i = 0; i < T; i++) {
				prob += Math.log(scalingFactors[i]);
		}
		
		prob = -prob;
		
		System.out.println(prob);
	}
	
	static void computeIterations() {
		
		iterations++;
		
		double delta = Math.abs(prob - oldProb);
		
		if(iterations < minIterations && eps < delta) {
			oldProb = prob;
			calls();
		}
		else {
			System.out.println(Arrays.deepToString(A));
			System.out.println(Arrays.deepToString(B));
			System.out.println(Arrays.toString(pi));
		}
		
	}
	
	public static void calls() {
		
		calculateAlpha();
		
		calculateBeta();
	
		gammaCalculations();	
	
		reestimations();
		
		computeProbability();
		
		initialPrint();
		
		computeIterations();
	}
	
	public static void main(String args[]) {
		
		
		initializeA_B_Pi();
		initialPrint();
		calls();
	
		}

}