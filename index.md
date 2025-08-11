# Michael Ongaro's ePortfolio | CS-499 | SNHU

### Professional Summary

<p>As I complete my Bachelor of Science in Computer Science at Southern New Hampshire University, I have developed a strong foundation in software engineering, algorithms, data structures, and database design, and I have applied these skills to create practical, industry-relevant solutions. My ePortfolio represents the culmination of my academic journey, showcasing enhanced projects that demonstrate my ability to design, develop, and deliver professional-quality software. Through this process, I have refined my technical expertise, strengthened my problem-solving abilities, and cultivated a professional mindset that values scalability, maintainability, and security in every solution I create. These enhancements not only reflect my growth as a developer but also my readiness to contribute meaningfully to collaborative, real-world software projects.</p>

<p>Throughout my program, I have learned the importance of building collaborative environments that support diverse audiences and organizational decision-making. In my code review process, I approached my work as if presenting to peers and stakeholders, clearly explaining existing functionality, identifying areas for improvement, and outlining enhancement plans. This experience reinforced my ability to communicate technical concepts in a way that is accessible to both technical and non-technical audiences. I have also embraced best practices in documentation, modular design, and version control, ensuring that my work is easy to understand, maintain, and extend by future collaborators. These skills will allow me to thrive in team-based environments where clear communication and shared understanding are essential.</p>

<p>My artifact enhancements demonstrate my ability to design and evaluate computing solutions using algorithmic principles and industry standards. For example, in my "Inventory Pro" application, I implemented an efficient Merge Sort algorithm to improve data organization and user experience, balancing performance with scalability. In my "Travlr" application, I integrated AWS S3 for cloud-based image storage, applying modern software architecture principles to decouple static assets from the application server. In my database enhancement, I re-architected the data layer to support a relational, folder-based structure, improving both usability and maintainability. Each of these projects required me to make thoughtful design trade-offs, anticipate potential vulnerabilities, and ensure that my solutions were both secure and adaptable to future needs.</p>

<p>Completing this ePortfolio has solidified my professional goals and values. I am committed to creating software that is not only functional but also secure, efficient, and user-focused. I take pride in my ability to integrate innovative techniques with well-founded practices, whether that means optimizing algorithms, designing scalable architectures, or implementing robust database solutions. My work reflects a security mindset, from configuring proper access controls in cloud integrations to enforcing data integrity in relational databases. As I move forward in my career, I am confident that the skills, experiences, and professional discipline I have developed through this program will enable me to deliver high-quality solutions that provide real value to organizations and their users.</p>

### Code Review

In this video, I walk through a detailed review of my project’s codebases, highlighting key areas for improvement and outlining the new features I plan to implement. For each major category: Software Engineering and Design, Algorithms and Data Structures, and Databases, I introduce specific problems that need to be addressed, from fixing bugs and optimizing existing logic to enhancing user experience and adding new capabilities. This review not only demonstrates my ability to critically analyze and improve my own work, but also sets the stage for the next steps in my development process.

(Click to watch ↓)
[![Michael Ongaro CS-499 Code Review(s)](https://img.youtube.com/vi/xMXppFz7a4g/maxresdefault.jpg)](https://www.youtube.com/watch?v=xMXppFz7a4g)

### Artifacts

<details open>
<summary><b>Software Engineering and Design</b></summary>

<p>
I selected my full-stack travel application "Travlr" as the artifact for my enhancement in the software engineering and design category. This application was originally developed during my CS-465 course and consisted of a client-facing website built with Express and Handlebars, along with an admin portal built with Angular that interfaced with a MongoDB database. The original implementation stored all trip images locally within the project's source code, which created significant limitations in terms of scalability, deployment, and maintenance. This design choice was functional for the original course requirements but did not align with industry best practices for handling static assets in a production environment.
</p>

<p>
I chose this artifact because it provided an excellent opportunity to demonstrate my understanding of modern software architecture principles, particularly the decoupling of static assets from application servers. The enhancement process involved integrating AWS S3 for cloud-based image storage and creating a reusable Angular component that consolidated the previously separate add and edit trip functionality. This implementation showcased my ability to design efficient, reusable components following the DRY (Don't Repeat Yourself) principle, as well as my skills in integrating third-party cloud services into existing applications. By refactoring the codebase to use a single component with conditional logic based on the current mode (add or edit), I demonstrated an understanding of component-based architecture that improves maintainability while reducing code duplication. I also added progress tracking for the image uploads which significantly improved the user experience compared to the original implementation.
</p>

<p>
Through this enhancement process, I successfully met course outcomes related to software engineering and design by implementing industry-standard practices for cloud integration and component reusability. I learned valuable lessons about the complexities involved in modifying existing systems, particularly when integrating new technologies and refactoring to improve architecture. The process required some more extensive modifications than I initially planned for, as modularizing the form components required changes across multiple files in the Angular codebase. One significant challenge I encountered involved configuring proper S3 bucket permissions and CORS policies, which required careful debugging using browser developer tools to identify and resolve issues with cross-origin requests. This experience strengthened my security mindset by emphasizing the importance of properly configured access controls when integrating external services, directly supporting the course outcome related to anticipating potential vulnerabilities and ensuring data security.
</p>

<p><b>Original Screenshots</b></p>

<img src="https://raw.githubusercontent.com/michaelongaro/michaelongaro.github.io/refs/heads/main/Artifact%201%20Original%20Screenshots/OriginalAddTrip.png" alt="Original Add Trip" />
<img src="https://raw.githubusercontent.com/michaelongaro/michaelongaro.github.io/refs/heads/main/Artifact%201%20Original%20Screenshots/OriginalEditTrip.png" alt="Original Edit Trip" />

<p><b>Enhanced Screenshots</b></p>

<img src="https://raw.githubusercontent.com/michaelongaro/michaelongaro.github.io/refs/heads/main/Artifact%201%20Enhanced%20Screenshots/NewAddTrip.png" alt="Enhanced Add Trip" />
<img src="https://raw.githubusercontent.com/michaelongaro/michaelongaro.github.io/refs/heads/main/Artifact%201%20Enhanced%20Screenshots/NewEditTrip.png" alt="Enhanced Edit Trip" />

<p><b>Files</b></p>
<ul>
  <li><a href="https://github.com/michaelongaro/michaelongaro.github.io/tree/main/Artifact%201%20Original%20Files">Artifact 1 Original Files</a></li>
  <li><a href="https://github.com/michaelongaro/michaelongaro.github.io/tree/main/Artifact%201%20Enhanced%20Files">Artifact 1 Enhanced Files</a></li>
</ul>

</details>

<details>
<summary><b>Algorithms and Data Structures</b></summary>

<p>I selected my Android inventory management application "Inventory Pro" as the artifact for my enhancement in the algorithms and data structure category. This application was originally developed during my CS-360 course and provided basic functionality for users to add, edit, and delete inventory items stored in a local SQLite database. The original implementation displayed inventory items in a simple, unsorted list based on the order they were retrieved from the database, which created significant usability issues as the inventory size grew. Users had no efficient way to locate specific items or analyze their inventory data, making the application impractical for real-world use cases where quick access to organized information is essential.</p>
<p>I chose this artifact because it provided an excellent opportunity to demonstrate my understanding of fundamental algorithmic principles and their practical application in solving real-world problems. The enhancement process involved implementing an efficient sorting system using the Merge Sort algorithm, which guaranteed O(n log n) performance regardless of dataset size. This implementation showcased my ability to analyze algorithmic complexity and select appropriate solutions that balance performance with scalability requirements. I created a comprehensive sorting framework that included multiple comparison criteria (item name, quantity, and creation date), bidirectional sorting options, and a generic utility class that could be extended for future enhancements. The solution required adding a new createdAt field to the database schema, implementing custom comparator classes following the Strategy design pattern, and creating an intuitive user interface with dropdown controls that allowed users to easily change sorting preferences.</p>
<p>I successfully met course outcomes related to designing and evaluating computing solutions using algorithmic principles and computer science best practices. The implementation demonstrated my ability to use well-founded techniques to deliver tangible value by transforming an inefficient, unsorted list into a highly organized and user-friendly interface. I learned valuable lessons about the importance of choosing appropriate algorithms for specific use cases, particularly how Merge Sort's stable sorting behavior and guaranteed performance made it superior to simpler alternatives like Bubble Sort or Selection Sort for this application. The process required more extensive database modifications than I initially realized, as I needed to implement proper schema migration to handle existing inventory items that lacked creation timestamps while ensuring backward compatibility.</p>
<p>One significant challenge I encountered involved refamiliarizing myself with the recursive nature of merge sort implementation and ensuring the algorithm worked correctly with custom comparator objects. I had to carefully debug the merging logic to handle edge cases where inventory items had null or empty values in their comparison fields. Another complexity came up during database schema modification, where I needed to design an upgrade strategy that would add the new createdAt column to existing installations without losing user data. This experience strengthened my understanding of both theoretical algorithmic concepts and practical software engineering considerations, particularly the importance of defensive programming when handling user data and the complexities involved in evolving database schemas in deployed applications.</p>


<p><b>Original Screenshots</b></p>

<img src="https://raw.githubusercontent.com/michaelongaro/michaelongaro.github.io/refs/heads/main/Artifact%202%20Original%20Screenshots/MainMenu.png" alt="Main page" />

<p><b>Enhanced Screenshots</b></p>

<img src="https://raw.githubusercontent.com/michaelongaro/michaelongaro.github.io/refs/heads/main/Artifact%202%20Enhanced%20Screenshots/MainPage.png" alt="Main page" />
<img src="https://raw.githubusercontent.com/michaelongaro/michaelongaro.github.io/refs/heads/main/Artifact%202%20Enhanced%20Screenshots/SortingMenuOpen.png" alt="Sorting menu open" />

<p><b>Files</b></p>
<ul>
  <li><a href="https://github.com/michaelongaro/michaelongaro.github.io/tree/main/Artifact%202%20Original%20Files">Artifact 2 Original Files</a></li>
  <li><a href="https://github.com/michaelongaro/michaelongaro.github.io/tree/main/Artifact%202%20Enhanced%20Files">Artifact 2 Enhanced Files</a></li>
</ul>

</details>

<details>
<summary><b>Databases</b></summary>

<p>For my database category artifact, I chose to further enhance my Android application, "Inventory Pro". It had a core limitation of storing every item in a single, flat table in the SQLite database. This design proved to be non-scalable and quite inefficient. As a user's inventory grew, the application presented a monolithic, unsorted list, making it nearly impossible to organize, categorize, or efficiently locate specific items. This fundamental design flaw made the application impractical for any serious use and presented a perfect opportunity to demonstrate my skills in relational database design and data-driven application development.</p>
<p>The primary goal of this enhancement was to re-architect the application's data layer to support a hierarchical, folder-based organization system, transforming it from a simple list into a structured management tool. This was achieved by fundamentally redesigning the SQLite database schema. I introduced a new Folders table and established a one-to-many relationship with the existing Items table by adding a folder_id foreign key. This enhancement showcases several key database and software engineering skills. First, it demonstrates relational database design, as I modeled a real-world requirement (categorization) into a normalized schema with primary and foreign keys. Second, it highlights my ability to implement advanced SQL and data manipulation by developing full CRUD (Create, Read, Update, Delete) operations for the new Folders table and updating the item-related queries to manage the new relationship. A major component of this was enforcing data integrity. For instance, the logic now prevents the deletion of a folder if it still contains items, protecting the user from accidental data loss. Finally, the project required full-stack integration, as the database changes required a complete overhaul of the user experience, from the backend queries to the frontend UI.</p>
<p>This enhancement successfully meets the course outcomes I planned, particularly Outcome 4 (Use Well-Founded Techniques). By replacing a flat-file structure with a proper relational model, I implemented a standard, well-founded database technique that delivered immense value in usability and organization. The project also aligns with Outcome 3 (Design and Evaluate Solutions), as the new architecture is a far superior solution to the problem of data organization, directly addressing the trade-offs of the original, simplistic design. I also achieved Outcome 1 (Employ strategies for building collaborative environments) during this artifact enhancement. The refactoring process forced a thoughtful reorganization of the entire application into a consistent, logical project structure. By separating concerns into distinct components the codebase became highly modular. This modularity, combined with properly documented code that explains the purpose and logic of each function, significantly reduces the barrier to entry for new team members. A future developer can now quickly become familiarized with the project, understand the data flow, and contribute effectively without needing to navigate a monolithic, complex system.</p>
<p>The process of implementing this enhancement was quite a learning experience for me. I again initially underestimated the scope of the changes required. I learned that modifying the database was not an isolated task, it required creating an entirely new FolderContentsActivity to display items within a folder, registering this new activity in the AndroidManifest.xml, and developing a new FolderAdapter for the main screen's RecyclerView. The original DashboardActivity had to be refactored to manage folders instead of items, and the AddEditItemActivity required significant modification to include the folder selection logic.</p>
<p>I faced several technical challenges that strengthened my practical skills. The most significant was implementing a database migration strategy. To ensure existing users wouldn't lose their data upon updating, I used the onUpgrade method in my DatabaseHelper to programmatically create a default "General" folder and then execute a SQL query to migrate all existing items into it. Another challenge involved debugging the application's state management, particularly an Activity lifecycle bug where a NullPointerException occurred because the DatabaseHelper was being used before it was initialized. This reinforced the importance of understanding the precise order of operations in Android's onCreate method. Lastly, managing the dynamic UI in the "Add/Edit Item" screen, where the folder selection controls could overlap, required me to restructure the ConstraintLayout using a FrameLayout as a stable container. This taught me how to better build robust and adaptive user interfaces.</p>


<p><b>Original Screenshots</b></p>

<img src="https://raw.githubusercontent.com/michaelongaro/michaelongaro.github.io/refs/heads/main/Artifact%203%20Original%20Screenshots/MainMenu.png" alt="Main menu" />
<img src="https://raw.githubusercontent.com/michaelongaro/michaelongaro.github.io/refs/heads/main/Artifact%203%20Original%20Screenshots/AddEditItem.png" alt="Add/Edit item" />

<p><b>Enhanced Screenshots</b></p>

<img src="https://raw.githubusercontent.com/michaelongaro/michaelongaro.github.io/refs/heads/main/Artifact%203%20Enhanced%20Screenshots/noItemsPage.png" alt="No items page" />
<img src="https://raw.githubusercontent.com/michaelongaro/michaelongaro.github.io/refs/heads/main/Artifact%203%20Enhanced%20Screenshots/mainFoldersView.png" alt="Main folders view" />
<img src="https://raw.githubusercontent.com/michaelongaro/michaelongaro.github.io/refs/heads/main/Artifact%203%20Enhanced%20Screenshots/individualFolder.png" alt="Individual folder" />
<img src="https://raw.githubusercontent.com/michaelongaro/michaelongaro.github.io/refs/heads/main/Artifact%203%20Enhanced%20Screenshots/addEditItem.png" alt="Add/Edit item" />


<p><b>Files</b></p>
<ul>
  <li><a href="https://github.com/michaelongaro/michaelongaro.github.io/tree/main/Artifact%203%20Original%20Files">Artifact 3 Original Files</a></li>
  <li><a href="https://github.com/michaelongaro/michaelongaro.github.io/tree/main/Artifact%203%20Enhanced%20Files">Artifact 3 Enhanced Files</a></li>
</ul>

</details>
