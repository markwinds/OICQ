outputSize=[100 200 3];
old_pic='profile';
new_name='small';
rate=0.1;

Img=imread(['.\app\src\main\res\drawable\' old_pic '.jpg']);
Img = imresize(Img,rate);                 %ת����С
%     delete([dirsBad old_name]);       %ɾ��ԭ�ļ�
%     new_name=num2str(i,'%04d');       %�����ת��Ϊ�ַ���Ϊ�ļ���  ����ע��һ��Ҫ��%04d����Ȼinwrite��������ͼƬ���ܻḲ�ǻ�δת����ͬ����ͼƬ
imwrite(Img,['.\app\src\main\res\drawable\' new_name '.jpg']);%�洢ͼƬ