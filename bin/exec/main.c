#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include <time.h>

#define MAX_LINE_LENGTH 1024

#pragma pack(push, 1)
typedef struct {
    unsigned char signature[2];
    unsigned int fileSize;
    unsigned int reserved;
    unsigned int dataOffset;
} BMPHeader;

typedef struct {
    unsigned int headerSize;
    int width;
    int height;
    unsigned short planes;
    unsigned short bitsPerPixel;
    unsigned int compression;
    unsigned int imageSize;
    int xPixelsPerMeter;
    int yPixelsPerMeter;
    unsigned int colorsUsed;
    unsigned int importantColors;
} DIBHeader;

#pragma pack(pop)

void swap (int * a, int * b)
{
	int temp = *a;
	*a = *b;
	*b = temp;
}

void randomize (int * arr, int n)
{
	srand (time(NULL));

	for (int i = n-1; i > 0; i--)
	{
		int j = rand() % (i+1);
		swap(&arr[i], &arr[j]);
	}
}

void saveTXT(char* filename, double * values, int dimension)
{
    FILE *file = fopen(filename, "a");
    if (file == NULL) {
        perror("Error opening file");
        return;
    }

    for (int i = 0; i < dimension; i++) {
        fprintf(file, "%f ", values[i]);
    }
    fprintf(file, "\n");

    fclose(file);
}

double test(double * use, double * xs, double * w, int dimension, int sample)
{
    for(int i= 0 ; i < sample; i++)
    {
        double tmp  = 0;
        for(int j = 0; j < dimension; j++)
        {   
            tmp += w[j] * xs[i*dimension + j];
        }
        use[i] = tanh(tmp);
    }
    double c = 0;
    for(int i = 0; i < sample; i++) 
    {
        if ((i < sample/2) && use[i] > 0) {c++;}
        else if ((i > sample/2) && use[i] < 0) {c++;}
    }
    return c;
}

char * concat(int number, const char * pathptr, const char * extensionptr)
{
    const char * path = pathptr;
    int pathLen = strlen(path);
    char str[25];
    sprintf(str, "%d", number);
    int strLen = strlen(str);
    const char * extension = extensionptr;
    char * fileName = calloc(100, sizeof(char));
    for(int i = 0; i < 100; i++) fileName[i] = '\0';
    for(int i = 0; i < 4+strLen+pathLen; i++)
    {
        if(i < pathLen) fileName[i] = path[i];
        else if(i < strLen+pathLen) fileName[i] = str[i-pathLen];
        else fileName[i] = extension[i-strLen-pathLen];
    }
    return fileName;
}

double * readBMP(const char *filename) 
{
    FILE *file = fopen(filename, "rb");
    if (!file) {
        perror("Error opening file");
        return NULL;
    }

    BMPHeader bmpHeader;
    fread(&bmpHeader, sizeof(BMPHeader), 1, file);

    DIBHeader dibHeader;
    fread(&dibHeader, sizeof(DIBHeader), 1, file);

    if (bmpHeader.signature[0] != 'B' || bmpHeader.signature[1] != 'M') 
    {
        printf("Not a valid BMP file.\n");
        fclose(file);
        return NULL;

    }

    if (dibHeader.bitsPerPixel != 8) 
    {
        printf("This program supports only 8-bit BMP files.\n");
        fclose(file);
        return NULL;
    }

    int width = dibHeader.width;
    int height = dibHeader.height;
    int rowSize = (width + 3) & ~3;

    double * matrix = calloc (width * height, sizeof(double));

    fseek(file, bmpHeader.dataOffset, SEEK_SET);

    unsigned char *row = malloc(rowSize);
    for (int i = 0; i < height; i++) 
    {
        fread(row, rowSize, 1, file);
        for (int j = 0; j < width; j++) 
        {
            matrix[(height-1-i)*width + j] = (double)((int)row[j]) / 255.0f;
        }
    }

    free(row);

    fclose(file);

    return matrix;
}

void readPhotos(double * use, const char * path1, const char * path2, int dimension, int sample1, int sample2, int startPoint1, int startPoint2)
{
    char ext[] = ".bmp";
    for(int i = 0; i < sample1 + sample2; i++)
    {
        double * tmp = calloc(dimension, sizeof(double));

        if(i < sample1) tmp =  readBMP(concat(i+startPoint1,path1,ext));
        else tmp =  readBMP(concat(i+startPoint2 - sample1,path2,ext));

        for(int j = 0; j < dimension-1; j++) use[i*dimension + j] = tmp[j];
        use[i*dimension + dimension-1] = 1;
    }
}

void error(double * use, double * xs, double * ys, double * w, int sample, int dimension)
{
    for(int i= 0 ; i < sample; i++)
    {
        double tmp  = 0;
        for(int j = 0; j < dimension; j++)
        {   
            tmp += w[j] * xs[i*dimension + j];
        }
        use[i] = (ys[i] - tanh(tmp));
    }
}

void jacobian(double * use, double * xs, double * ys, double * w, int sample, int dimension)
{
    double * result;
    result = calloc(sample*dimension, sizeof(double));
    
    for(int n = 0 ; n < sample; n++)
    {
        double tmp = 0;
        for(int d = 0; d < dimension; d++) {tmp += w[d] * xs[n*dimension + d];}
        double sech2 = (1/(cosh(tmp) * cosh(tmp)));

        for(int d = 0; d < dimension; d++)
        {   
            result[n*dimension + d] = -1 * sech2 * xs[n*dimension + d];
        }
    }

    //transpose
    for(int i = 0 ; i < sample; i++)
    {
        for(int j = 0; j < dimension; j++)
        {   
            use[j*sample + i] = result[i*dimension + j];
        }
    }
    free(result);
}

double errorIndex(int sample_index, double * xs, double * ys, double * w, int dimension)
{
    double result = 0;
    double tmp  = 0;

    for(int j = 0; j < dimension; j++)
    {   
        tmp += w[j] * xs[sample_index*dimension + j];
    }
    
    result = (ys[sample_index] - tanh(tmp));

    return result;
}

void gradientIndex(double * use, int sample_index, double * xs, double * ys, double * w, int dimension)
{    
    double tmp = 0;

    for(int d = 0; d < dimension; d++) {tmp += w[d] * xs[sample_index*dimension + d];}

    double sech2 = 1/(cosh(tmp)*cosh(tmp));

    for(int d = 0; d < dimension; d++)
    {
        use[d] = -1 * sech2 * xs[sample_index*dimension + d];
    }
}

void GD
(double stepSize, double * ysTrain, double * ysTest, double * xsTrain, double * xsTest, 
double * w, int dimension, int sampleTrain, int sampleTest, double threshold)
{
    double * result;
    result = calloc(dimension, sizeof(double));

    double * jacobianTranspose;
    jacobianTranspose = calloc(dimension * sampleTrain, sizeof(double));

    double * residuals = calloc(sampleTrain, sizeof(double));
    error(residuals, xsTrain, ysTrain, w, sampleTrain, dimension);

    double t = dimension;

    int iteration = 0;
    int maxIteration = 600;


    saveTXT("data/gd.txt", w, dimension);

    clock_t start, end;
    double * time = calloc(maxIteration+1, sizeof(double));

    double * lossTrain = calloc(maxIteration+1, sizeof(double));
    double * lossTest = calloc(maxIteration+1, sizeof(double));

    double * correctTrain = calloc(maxIteration+1, sizeof(double));
    double * correctTest = calloc(maxIteration+1, sizeof(double));
 
    double * errorTrain = calloc(sampleTrain, sizeof(double));
    double lTrain = 0;
    double * errorTest = calloc(sampleTest, sizeof(double));
    double lTest = 0;

    double * ysTrainUse = calloc(sampleTrain, sizeof(double));
    double * ysTestUse = calloc(sampleTrain, sizeof(double));

    while((t/dimension > threshold && (lTrain/sampleTrain) > 0.2f && iteration < maxIteration) 
    || iteration < 16)
    {
        start = clock();

        error(errorTrain, xsTrain, ysTrain, w, sampleTrain, dimension);
        lTrain = 0;
        for(int i = 0; i < sampleTrain; i++) {lTrain += pow(errorTrain[i],2);}
        lossTrain[iteration] = (lTrain/sampleTrain);

        error(errorTest, xsTest, ysTest, w, sampleTest, dimension);
        lTest = 0;
        for(int i = 0; i < sampleTest; i++) {lTest += pow(errorTest[i],2);}
        lossTest[iteration] = (lTest/sampleTest);

        correctTrain[iteration] = test(ysTrainUse, xsTrain, w, dimension, sampleTrain) / sampleTrain;
        correctTest[iteration] = test(ysTestUse, xsTest, w, dimension, sampleTest) / sampleTest;

        jacobian(jacobianTranspose, xsTrain, ysTrain, w, sampleTrain, dimension);
        error(residuals, xsTrain, ysTrain, w, sampleTrain, dimension);

        t = 0;

        for(int i = 0; i < dimension; i++)
        {
            double tmp = 0;
            for(int j = 0; j < sampleTrain; j++)
            {
                tmp += jacobianTranspose[i*sampleTrain + j] * residuals[j];
            }
            result[i] = w[i] - 2 * stepSize * tmp;
            t += fabs(2 * stepSize * tmp);
        }
        iteration++;
        w = result;
        saveTXT("data/gd.txt", w, dimension);


        end = clock();
        if (iteration == 1)
            time[iteration-1] = ((double)(end - start)) / CLOCKS_PER_SEC;
        else
            time[iteration-1] = time[iteration-2] + ((double)(end - start)) / CLOCKS_PER_SEC;
    }

    double * iterations = calloc(iteration, sizeof(double));
    for(int i  = 0; i < iteration; i++) {iterations[i] = 0.00f + i;}

    saveTXT("data/gdIteration.txt", iterations, iteration);
    saveTXT("data/gdTime.txt", time, iteration);
    saveTXT("data/gdLossTrain.txt", lossTrain, iteration);
    saveTXT("data/gdLossTest.txt", lossTest, iteration);
    saveTXT("data/gdCorrectTrain.txt", correctTrain, iteration);
    saveTXT("data/gdCorrectTest.txt", correctTest, iteration);
}

void SGD
(double stepSize, double * ysTrain, double * ysTest, double * xsTrain, double * xsTest ,
double * w, int dimension, int sampleTrain, int sampleTest, double threshold)
{
    double * result;
    result = calloc(dimension, sizeof(double));

    double * gradientindex;
    gradientindex = calloc(dimension , sizeof(double));

    double residual = 0;

    int * index = calloc(sampleTrain, sizeof(int));
    for(int i = 0; i < sampleTrain; i++) {index[i] = i;}
    randomize(index, sampleTrain);
    
    double t = dimension;
    int iteration = 0;
    int flag = 0;
    int maxIteration = 600;

    saveTXT("data/sgd.txt", w, dimension);

    clock_t start, end;
    double * time = calloc(maxIteration+1, sizeof(double));

    double * lossTrain = calloc(maxIteration+1, sizeof(double));
    double * lossTest = calloc(maxIteration+1, sizeof(double));

    double * correctTrain = calloc(maxIteration+1, sizeof(double));
    double * correctTest = calloc(maxIteration+1, sizeof(double));
 
    double * errorTrain = calloc(sampleTrain, sizeof(double));
    double lTrain = 0;
    double * errorTest = calloc(sampleTest, sizeof(double));
    double lTest = 0;

    double * ysTrainUse = calloc(sampleTrain, sizeof(double));
    double * ysTestUse = calloc(sampleTrain, sizeof(double));

    while((t/dimension > threshold && (lTrain/sampleTrain) > 0.2f && iteration < maxIteration) 
    || iteration < 16)
    {
        start = clock();

        error(errorTrain, xsTrain, ysTrain, w, sampleTrain, dimension);
        lTrain = 0;
        for(int i = 0; i < sampleTrain; i++) {lTrain += pow(errorTrain[i],2);}
        lossTrain[iteration] = (lTrain/sampleTrain);

        error(errorTest, xsTest, ysTest, w, sampleTest, dimension);
        lTest = 0;
        for(int i = 0; i < sampleTest; i++) {lTest += pow(errorTest[i],2);}
        lossTest[iteration] = (lTest/sampleTest);

        correctTrain[iteration] = test(ysTrainUse, xsTrain, w, dimension, sampleTrain) / sampleTrain;
        correctTest[iteration] = test(ysTestUse, xsTest, w, dimension, sampleTest) / sampleTest;
        
        gradientIndex(gradientindex, index[iteration%sampleTrain], xsTrain, ysTrain, w, dimension);
        residual = errorIndex(index[iteration%sampleTrain], xsTrain, ysTrain, w, dimension);

        t = 0;

        for(int i = 0; i < dimension; i++)
        {
            result[i] = w[i] - 2 * stepSize * gradientindex[i] * residual;
            t += fabs(2 * stepSize * gradientindex[i] * residual);
        }

        iteration++;

        w = result;
        saveTXT("data/sgd.txt", w, dimension);

        end = clock();
        if (iteration == 1)
            time[iteration-1] = ((double)(end - start)) / CLOCKS_PER_SEC;
        else
            time[iteration-1] = time[iteration-2] + ((double)(end - start)) / CLOCKS_PER_SEC;
    }

    double * iterations = calloc(iteration, sizeof(double));
    for(int i  = 0; i < iteration; i++) iterations[i] = (double)i;

    saveTXT("data/sgdIteration.txt", iterations, iteration);
    saveTXT("data/sgdTime.txt", time, iteration);
    saveTXT("data/sgdLossTrain.txt", lossTrain, iteration);
    saveTXT("data/sgdLossTest.txt", lossTest, iteration);
    saveTXT("data/sgdCorrectTrain.txt", correctTrain, iteration);
    saveTXT("data/sgdCorrectTest.txt", correctTest, iteration);
}

void ADAM
(double stepSize, double * ysTrain, double * ysTest, double * xsTrain, double * xsTest, 
double * w, int dimension, int sampleTrain, int sampleTest, double threshold, double B1, double B2)
{
    double * result;
    result = calloc(dimension, sizeof(double));

    double * gt;
    gt = calloc(dimension , sizeof(double));

    double residual = 0;

    double * firstmoment = calloc(dimension, sizeof(double));
    for(int i = 0; i < dimension; i++) {firstmoment[i] = 0;}
    double * secondmoment = calloc(dimension, sizeof(double));
    for(int i = 0; i < dimension; i++) {secondmoment[i] = 0;}

    double firstmoment_biascorrect = 0;
    double secondmoment_biascorrect = 0;

    double dummy = 0;
    double t = dimension;
    
    int * index = calloc(sampleTrain, sizeof(int));
    for(int i = 0; i < sampleTrain; i++) {index[i] = i;}
    randomize(index, sampleTrain);

    int iteration = 0;
    int maxIteration = 1500;

    saveTXT("data/adam.txt", w, dimension);
    clock_t start, end;
    double * time = calloc(maxIteration+1, sizeof(double));

    double * lossTrain = calloc(maxIteration+1, sizeof(double));
    double * lossTest = calloc(maxIteration+1, sizeof(double));

    double * correctTrain = calloc(maxIteration+1, sizeof(double));
    double * correctTest = calloc(maxIteration+1, sizeof(double));

    double * errorTrain = calloc(sampleTrain, sizeof(double));
    double lTrain = 0;
    double * errorTest = calloc(sampleTest, sizeof(double));
    double lTest = 0;

    double * ysTrainUse = calloc(sampleTrain, sizeof(double));
    double * ysTestUse = calloc(sampleTrain, sizeof(double));
    
    while((t/dimension > threshold && (lTrain/sampleTrain) > 0.4f && iteration < maxIteration) 
    || iteration < 16)
    {
        start = clock();

        error(errorTrain, xsTrain, ysTrain, w, sampleTrain, dimension);
        lTrain = 0;
        for(int i = 0; i < sampleTrain; i++) {lTrain += pow(errorTrain[i],2);}
        lossTrain[iteration] = (lTrain/sampleTrain);

        error(errorTest, xsTest, ysTest, w, sampleTest, dimension);
        lTest = 0;
        for(int i = 0; i < sampleTest; i++) {lTest += pow(errorTest[i],2);}
        lossTest[iteration] = (lTest/sampleTest);

        correctTrain[iteration] = test(ysTrainUse, xsTrain, w, dimension, sampleTrain) / sampleTrain;
        correctTest[iteration] = test(ysTestUse, xsTest, w, dimension, sampleTest) / sampleTest;
        
        gradientIndex(gt, index[iteration%sampleTrain], xsTrain, ysTrain, w, dimension);
        residual = 2 * errorIndex(index[iteration%sampleTrain], xsTrain, ysTrain, w, dimension);

        t = 0;
        for(int i = 0; i < dimension; i++)
        {
            firstmoment[i] = B1 * firstmoment[i] +  (1 - B1) * gt[i] * residual;
            secondmoment[i] = B2 * secondmoment[i] +  (1 - B2) * gt[i] * gt[i] * residual * residual;
            
            firstmoment_biascorrect = firstmoment[i] / (1- pow(B1,iteration+1));
            secondmoment_biascorrect = secondmoment[i] / (1- pow(B2,iteration+1));

            dummy = stepSize * ( firstmoment_biascorrect / (pow(secondmoment_biascorrect,0.5f) + pow(10,-8)) );
            result[i] = w[i] - dummy;
            t += fabs(dummy);
        }
        iteration++;
        w = result;
        saveTXT("data/adam.txt", w, dimension);

        end = clock();
        if (iteration == 1)
            time[iteration-1] = ((double)(end - start)) / CLOCKS_PER_SEC;
        else
            time[iteration-1] = time[iteration-2] + ((double)(end - start)) / CLOCKS_PER_SEC;
    }
    double * iterations = calloc(iteration, sizeof(double));
    for(int i  = 0; i < iteration; i++) iterations[i] = (double)i;

    saveTXT("data/adamIteration.txt", iterations, iteration);
    saveTXT("data/adamTime.txt", time, iteration);
    saveTXT("data/adamLossTrain.txt", lossTrain, iteration);
    saveTXT("data/adamLossTest.txt", lossTest, iteration);
    saveTXT("data/adamCorrectTrain.txt", correctTrain, iteration);
    saveTXT("data/adamCorrectTest.txt", correctTest, iteration);
}

double * parameters(const char * path, int * isValid, int count)
{
    FILE * file = fopen(path, "r");
    if (file == NULL) {
        *isValid = -1;
    }
    else
    {
        *isValid = 1;
    }

    char line[MAX_LINE_LENGTH];
    const char * str;
    char * endptr;
    double * value = calloc(count, sizeof(double));
    fgets(line, sizeof(line), file);//for headers
    int i = 0;
    while (fgets(line, sizeof(line), file)) 
    {
        str = line;
        value[i] = strtod(str, &endptr);
        //printf("%f\n", value[i]);
        //printf("%s\n", line);
        i++;
    }
    value[i] = (double)-1; // sentinel value
    return value;
}

int main() 
{
    int gdIsValid = 0;
    double * gdParameters = calloc(10, sizeof(double));
    gdParameters = parameters("data/gd.dat", &gdIsValid, 10);

    int sgdIsValid = 0;
    double * sgdParameters = calloc(10, sizeof(double));
    sgdParameters = parameters("data/sgd.dat", &sgdIsValid,10);

    int adamIsValid = 0;
    double * adamParameters = calloc(10, sizeof(double));
    adamParameters = parameters("data/adam.dat", &adamIsValid,10);

    char path1[]   = "img/class1/";
    char path2[]   = "img/class2/";

    int photosIsValid = 0;
    double * photoParameters = calloc(10, sizeof(double));
    photoParameters = parameters("img/photo.dat", &photosIsValid,10);

    int dimension     = photoParameters[5]*photoParameters[5]+1;  
    int percentage    = photoParameters[4];
    int sample1Images = photoParameters[1];
    int sample2Images = photoParameters[3];
    int sample1Train  = (int)(sample1Images*((double)percentage/100));
    int sample1Test   = sample1Images - sample1Train;
    int sample2Train  = (int)(sample2Images*((double)percentage/100));
    int sample2Test   = sample2Images - sample2Train;
    int sampleTrain   = sample1Train + sample2Train;
    int sampleTest    = sample1Test + sample2Test;

    double * xsTrain = calloc (dimension*sampleTrain, sizeof(double));
    readPhotos(xsTrain, path1, path2, dimension, sample1Train, sample2Train, 0, 0);

    double * xsTest = calloc (dimension*sampleTest, sizeof(double));
    readPhotos(xsTest, path1, path2, dimension, sample1Test, sample2Test, sample1Train, sample2Train);
    
   
    double * ysTrain = calloc (sampleTrain, sizeof(double));
    for(int i = 0; i < sampleTrain; i++)
    {
        if(i < sample1Train) ysTrain[i] = 1;
        else ysTrain[i] = -1;
    }
    
    double * ysTest = calloc (sampleTest, sizeof(double));
    for(int i = 0; i < sampleTest; i++) 
    {
        if(i < sample1Test) ysTest[i] = 1; 
        else ysTest[i] = -1;
    }
    int wIsValid = 0;
    double * w = calloc(dimension, sizeof(double));
    w = parameters("data/w.dat", &wIsValid, dimension);

    if(wIsValid == 1)
    {
        if(gdIsValid == 1)
        GD  (gdParameters[0],   ysTrain, ysTest, xsTrain, xsTest, w, dimension, sampleTrain, sampleTest, gdParameters[1]);
        if(sgdIsValid == 1)
            SGD (sgdParameters[0],  ysTrain, ysTest, xsTrain, xsTest, w, dimension, sampleTrain, sampleTest, sgdParameters[1]);
        if(adamIsValid == 1)
            ADAM(adamParameters[0], ysTrain ,ysTest, xsTrain, xsTest, w, dimension, sampleTrain, sampleTest, adamParameters[3], 
        adamParameters[1], adamParameters[2]);
    }

    return 0;
}
