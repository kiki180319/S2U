import json

members_data = [
  {
    "stageName": "Jiwoo",
    "fullName": "Choi Ji-woo",
    "nativeName": "최지우",
    "birthday": "2006-09-07",
    "position": "Leader, Dancer, Rapper",
    "heightCm": 169.0,
    "bloodType": "A",
    "mbti": "ISTJ",
    "birthplace": "Seoul, South Korea",
    "nationality": "South Korean",
    "imageUrl": "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/jiwoo.jpg"
  },
  {
    "stageName": "Carmen",
    "fullName": "Nyoman Ayu Carmenita",
    "nativeName": "Nyoman Ayu Carmenita",
    "birthday": "2006-03-28",
    "position": "Dancer, Vocalist",
    "heightCm": 168.0,
    "bloodType": "O",
    "mbti": "ENFJ",
    "birthplace": "Denpasar, Bali, Indonesia",
    "nationality": "Indonesian",
    "imageUrl": "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/carmen.jpeg"
  },
  {
    "stageName": "Yuha",
    "fullName": "Yu Ha-ram",
    "nativeName": "유하람",
    "birthday": "2007-04-12",
    "position": "Main Vocalist, Lead Dancer",
    "heightCm": 164.0,
    "bloodType": "B",
    "mbti": "INTJ",
    "birthplace": "Wonju, Gangwon-do, South Korea",
    "nationality": "South Korean",
    "imageUrl": "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/yuha.webp"
  },
  {
    "stageName": "Stella",
    "fullName": "Kim Da-hyun",
    "nativeName": "김다현",
    "birthday": "2007-06-18",
    "position": "Vocalist, Visual",
    "heightCm": 168.5,
    "bloodType": "O",
    "mbti": "INFP",
    "birthplace": "Ulsan, South Korea",
    "nationality": "South Korean / Canadian",
    "imageUrl": "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/stella.jpg"
  },
  {
    "stageName": "Juun",
    "fullName": "Kim Ju-eun",
    "nativeName": "김주은",
    "birthday": "2008-12-03",
    "position": "Vocalist",
    "heightCm": 166.0,
    "bloodType": "A",
    "mbti": "ISFJ",
    "birthplace": "Ilsan, Gyeonggi-do, South Korea",
    "nationality": "South Korean",
    "imageUrl": "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/juun.png"
  },
  {
    "stageName": "A-na",
    "fullName": "Roh Yu-na",
    "nativeName": "노유나",
    "birthday": "2008-12-20",
    "position": "Rapper, Dancer",
    "heightCm": 171.0,
    "bloodType": "A",
    "mbti": "ESFP",
    "birthplace": "Suwon, Gyeonggi-do, South Korea",
    "nationality": "South Korean",
    "imageUrl": "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/ana.jpeg"
  },
  {
    "stageName": "Ian",
    "fullName": "Jeong Lee-an",
    "nativeName": "정이안",
    "birthday": "2009-10-09",
    "position": "Vocalist, Dancer",
    "heightCm": 164.0,
    "bloodType": "B",
    "mbti": "ENFP",
    "birthplace": "Seoul, South Korea",
    "nationality": "South Korean",
    "imageUrl": "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/ian.jpeg"
  },
  {
    "stageName": "Ye-on",
    "fullName": "Kim Na-yeon",
    "nativeName": "김나연",
    "birthday": "2010-04-19",
    "position": "Vocalist, Maknae",
    "heightCm": 166.0,
    "bloodType": "A",
    "mbti": "INFJ",
    "birthplace": "Yangsan, Gyeongsangnam-do, South Korea",
    "nationality": "South Korean",
    "imageUrl": "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/yeon.jpg"
  }
]

videos_data = [
    {
      "id": "vid-h2h-001",
      "title": "Kenalan Sama Idola Baru Hearts2Hearts!",
      "type": "Teaser/Trailer",
      "youtube_id": "97lkHMfKCRE",
      "video_url": "https://www.youtube.com/watch?v=97lkHMfKCRE",
      "thumbnail_url": "https://img.youtube.com/vi/97lkHMfKCRE/0.jpg"
    },
    {
      "id": "vid-h2h-002",
      "title": "The Chase",
      "type": "Music Video",
      "youtube_id": "kxUA2wwYiME",
      "video_url": "https://www.youtube.com/watch?v=kxUA2wwYiME",
      "thumbnail_url": "https://img.youtube.com/vi/kxUA2wwYiME/0.jpg"
    },
    {
      "id": "vid-h2h-003",
      "title": "The Chase vs STYLE",
      "type": "Live Performance",
      "youtube_id": "18_WvzvQ31k",
      "video_url": "https://www.youtube.com/watch?v=18_WvzvQ31k",
      "thumbnail_url": "https://img.youtube.com/vi/18_WvzvQ31k/0.jpg"
    },
    {
      "id": "vid-h2h-004",
      "title": "STYLE",
      "type": "Music Video",
      "youtube_id": "n7kFRxFIPrI",
      "video_url": "https://www.youtube.com/watch?v=n7kFRxFIPrI",
      "thumbnail_url": "https://img.youtube.com/vi/n7kFRxFIPrI/0.jpg"
    },
    {
      "id": "vid-h2h-005",
      "title": "Pretty Please",
      "type": "Music Video",
      "youtube_id": "ufwB9Uja_wM",
      "video_url": "https://www.youtube.com/watch?v=ufwB9Uja_wM",
      "thumbnail_url": "https://img.youtube.com/vi/ufwB9Uja_wM/0.jpg"
    },
    {
      "id": "vid-h2h-006",
      "title": "FOCUS",
      "type": "Music Video",
      "youtube_id": "Ur7aK4FvK-U",
      "video_url": "https://www.youtube.com/watch?v=Ur7aK4FvK-U",
      "thumbnail_url": "https://img.youtube.com/vi/Ur7aK4FvK-U/0.jpg"
    },
    {
      "id": "vid-h2h-007",
      "title": "RUDE!",
      "type": "Music Video",
      "youtube_id": "F7sGJVUrkjQ",
      "video_url": "https://www.youtube.com/watch?v=F7sGJVUrkjQ",
      "thumbnail_url": "https://img.youtube.com/vi/F7sGJVUrkjQ/0.jpg"
    },
    {
      "id": "vid-h2h-008",
      "title": "Lemon Tang",
      "type": "Music Video",
      "youtube_id": "1VqxWNwgf5Q",
      "video_url": "https://www.youtube.com/watch?v=1VqxWNwgf5Q",
      "thumbnail_url": "https://img.youtube.com/vi/1VqxWNwgf5Q/0.jpg"
    }
  ]

with open('app/src/main/assets/data/members.json', 'w') as f:
    json.dump(members_data, f, indent=2)

with open('app/src/main/assets/data/videos.json', 'w') as f:
    json.dump(videos_data, f, indent=2)

