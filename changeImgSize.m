outputSize=[100 200 3];
old_pic='profile';
new_name='small';
rate=0.1;

Img=imread(['.\app\src\main\res\drawable\' old_pic '.jpg']);
Img = imresize(Img,rate);                 %转化大小
%     delete([dirsBad old_name]);       %删除原文件
%     new_name=num2str(i,'%04d');       %将标号转化为字符作为文件名  这里注意一定要加%04d，不然inwrite产生的新图片可能会覆盖还未转化的同名的图片
imwrite(Img,['.\app\src\main\res\drawable\' new_name '.jpg']);%存储图片